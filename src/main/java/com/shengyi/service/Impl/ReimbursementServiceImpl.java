
package com.shengyi.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shengyi.config.OperationType;
import com.shengyi.config.RedisCacheService;
import com.shengyi.dto.ReimbursementListDTO;
import com.shengyi.dto.ReimbursementSaveDTO;
import com.shengyi.entity.*;
import com.shengyi.exception.BusinessException;
import com.shengyi.exception.StatusCode;
import com.shengyi.mapper.ReimbursementMapper;
import com.shengyi.service.*;
import com.shengyi.vo.ReimbursementDetailVO;
import com.shengyi.vo.ReimbursementListVO;
import com.shengyi.vo.PageResult;
import com.shengyi.vo.SubsidySummaryVO;
import com.shengyi.vo.TripVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ReimbursementServiceImpl extends ServiceImpl<ReimbursementMapper, Reimbursement> implements ReimbursementService {
    @Autowired
    private ReimbursementMapper reimbursementMapper;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DepartService departService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private BusinessTypeService businessTypeService;
    @Autowired
    private RedisCacheService redisCacheService;
    @Autowired
    private AuditLogService auditLogService;

    @Override
    public PageResult<ReimbursementListVO> getReimbursementList(ReimbursementListDTO query) {
        query.setPageSize(Math.max(1, Math.min(query.getPageSize(), 100)));
        query.setPageNo(Math.max(1, query.getPageNo()));

        // 1. 查缓存（Hash 结构）
        String queryHash = RedisCacheService.computeQueryHash(query);
        PageResult<ReimbursementListVO> cached = redisCacheService.getReimbList(queryHash);
        
        // 检查并修复补助金额
        if (cached != null) {
            List<ReimbursementListVO> cachedRecords = cached.getRecords();
            if (cachedRecords != null && !cachedRecords.isEmpty()) {
                boolean needFix = false;
                for (ReimbursementListVO vo : cachedRecords) {
                    if (vo.getStatus() != null && vo.getStatus() == 1) {
                        if (vo.getSubsidyAmount() == null || vo.getSubsidyAmount().compareTo(BigDecimal.ZERO) == 0) {
                            needFix = true;
                            break;
                        }
                    }
                }
                
                // 如果需要修复，跳过缓存重新查询
                if (!needFix) {
                    return cached;
                }
            } else {
                return cached;
            }
        }

        // 3. 缓存未命中或需要修复 → 查 DB
        Page<ReimbursementListVO> page = Page.of(query.getPageNo(), query.getPageSize());
        Page<ReimbursementListVO> p = reimbursementMapper.selectReimbursementList(page, query);
        
        // 4. 自动修复补助金额不一致的问题
        List<ReimbursementListVO> records = p.getRecords();
        if (records != null && !records.isEmpty()) {
            for (ReimbursementListVO vo : records) {
                // 对于审批中的报销单，实时计算并修复补助金额
                if (vo.getStatus() != null && vo.getStatus() == 1) {
                    Long reimbursementId = vo.getReimbursementId();
                    if (reimbursementId != null) {
                        BigDecimal actualSubsidy = calculateTotalSubsidy(reimbursementId);
                        if (vo.getSubsidyAmount() == null || vo.getSubsidyAmount().compareTo(actualSubsidy) != 0) {
                            // 更新数据库中的补助金额
                            Reimbursement reimbursement = this.getById(reimbursementId);
                            if (reimbursement != null && reimbursement.getSubsidyAmount().compareTo(actualSubsidy) != 0) {
                                reimbursement.setSubsidyAmount(actualSubsidy);
                                reimbursementMapper.updateById(reimbursement);
                                // 更新 VO 中的金额
                                vo.setSubsidyAmount(actualSubsidy);
                            }
                        }
                    }
                }
            }
        }
        
        PageResult<ReimbursementListVO> pageResult = new PageResult<>(p.getTotal(), (int) p.getPages(), (int) p.getCurrent(), (int) p.getSize(), p.getRecords());

        // 5. 写入缓存
        redisCacheService.setReimbList(queryHash, pageResult);
        return pageResult;
    }

    @Override
    public ReimbursementDetailVO getReimbursementDetail(String reimbursementId) {
        Long id = Long.parseLong(reimbursementId);

        // 1. 查询报销单基本信息
        ReimbursementDetailVO detail = reimbursementMapper.selectReimbursementDetail(id);
        if (detail == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销单不存在");
        }

        // 2. 查询关联的补录行程列表
        List<TripVO> tripList = reimbursementMapper.selectTripsByReimbursementId(id);
        detail.setTripList(tripList != null ? tripList : Collections.emptyList());

        // 3. 查询补助信息汇总列表
        List<SubsidySummaryVO> subsidySummaryList = reimbursementMapper.selectSubsidySummaryByReimbursementId(id);
        detail.setSubsidySummaryList(subsidySummaryList != null ? subsidySummaryList : Collections.emptyList());

        return detail;
    }

    @Override
    @Transactional
    public Long saveDraft(ReimbursementSaveDTO dto) {
        // ---- 新建草稿 ----
        if (!StringUtils.hasText(dto.getReimbursementId())) {
            return createDraft(dto);
        }

        // ---- 更新草稿 ----
        Long id = Long.parseLong(dto.getReimbursementId());
        Reimbursement existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销单不存在");
        }
        if (existing.getStatus() != 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅草稿状态可编辑");
        }
        if (!existing.getVersion().equals(dto.getVersion())) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "数据已被他人修改，请刷新后重试");
        }

        // 必填校验
        validateDraftRequired(dto);

        // 构建更新对象
        Reimbursement update = new Reimbursement();
        update.setId(id);
        update.setVersion(dto.getVersion()); // 乐观锁条件
        update.setTitle(dto.getTitle());
        update.setReason(dto.getReason());
        update.setCompanyId(dto.getCompanyId());
        update.setDepartmentId(dto.getDepartmentId());
        update.setReimburserId(dto.getReimburserId());
        update.setBusinessTypeId(dto.getBusinessTypeId());
        update.setRemarks(dto.getRemarks());

        fillCompanyInfo(update);
        fillDepartmentInfo(update);
        fillEmployeeInfo(update);
        fillBusinessTypeInfo(update);

        int rows = reimbursementMapper.updateDraftWithVersion(update);
        if (rows == 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "更新失败：数据已被他人修改或状态已变更");
        }

        auditLogService.record(OperationType.REIMB_UPDATE, String.valueOf(id), existing, update);

        redisCacheService.evictReimbListCache();
        return id;
    }
    // ==================== 私有方法 ====================

    /**
     * 新建草稿
     */
    private Long createDraft(ReimbursementSaveDTO dto) {
        // 必填校验
        validateDraftRequired(dto);

        Reimbursement entity = new Reimbursement();
        entity.setReimbursementNo(generateReimbursementNo());
        entity.setTitle(dto.getTitle());
        entity.setReason(dto.getReason());
        entity.setRemarks(dto.getRemarks());
        entity.setStatus(0);
        entity.setVersion(0);
        entity.setSubsidyAmount(java.math.BigDecimal.ZERO.setScale(2));

        // 必填字段：直接赋值 + 补全 name/no
        entity.setCompanyId(dto.getCompanyId());
        entity.setDepartmentId(dto.getDepartmentId());
        entity.setReimburserId(dto.getReimburserId());
        entity.setBusinessTypeId(dto.getBusinessTypeId());

        fillCompanyInfo(entity);
        fillDepartmentInfo(entity);
        fillEmployeeInfo(entity);
        fillBusinessTypeInfo(entity);

        this.save(entity);

        auditLogService.record(OperationType.REIMB_CREATE, String.valueOf(entity.getId()), null, entity);

        redisCacheService.evictReimbListCache();
        return entity.getId();
    }

    /**
     * 生成报销单号：BX + yyyyMMdd + 6位随机数
     */
    private String generateReimbursementNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "BX" + date + random;
    }

    /**
     * 根据 reimburserId 补全报销人姓名和工号
     */
    private void fillEmployeeInfo(Reimbursement entity) {
        Employee emp = employeeService.getOne(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getReimburserId, entity.getReimburserId())
        );
        if (emp != null) {
            entity.setReimburserName(emp.getReimburserName());
            entity.setReimburserNo(emp.getReimburserNo());
        }
    }

    /**
     * 根据 departmentId 补全部门名称和编号
     */
    private void fillDepartmentInfo(Reimbursement entity) {
        Depart dep = departService.getOne(
                new LambdaQueryWrapper<Depart>()
                        .eq(Depart::getReimDepartmentId, entity.getDepartmentId())
        );
        if (dep != null) {
            entity.setDepartmentName(dep.getReimDepartmentName());
            entity.setDepartmentNo(dep.getReimDepartmentNo());
        }
    }

    /**
     * 根据 companyId 补全公司名称和编号
     */
    private void fillCompanyInfo(Reimbursement entity) {
        Company comp = companyService.getOne(
                new LambdaQueryWrapper<Company>()
                        .eq(Company::getReimCompanyId, entity.getCompanyId())
        );
        if (comp != null) {
            entity.setCompanyName(comp.getReimCompanyName());
            entity.setCompanyNo(comp.getReimCompanyNo());
        }
    }

    /**
     * 根据 businessTypeId 补全业务类型名称
     */
    private void fillBusinessTypeInfo(Reimbursement entity) {
        BusinessType bt = businessTypeService.getOne(
                new LambdaQueryWrapper<BusinessType>()
                        .eq(BusinessType::getBusinessTypeId, entity.getBusinessTypeId())
        );
        if (bt != null) {
            entity.setBusinessTypeName(bt.getBusinessTypeName());
        }
    }
    // ==================== 提交报销单 ====================

    @Override
    @Transactional
    public void submitReimbursement(ReimbursementSaveDTO dto) {
        Long id = Long.parseLong(dto.getReimbursementId());
        Reimbursement existing = this.getById(id);

        // 1. 基础校验：存在性 + 状态 + 乐观锁
        if (existing == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销单不存在");
        }
        if (existing.getStatus() != 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅草稿状态可提交");
        }
        if (!existing.getVersion().equals(dto.getVersion())) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "数据已被他人修改，请刷新后重试");
        }

        // 2. 全量校验：必填字段
        validateRequiredFields(existing);

        // 3. 行程非空校验
        if (reimbursementMapper.countTripsByReimbursementId(id) == 0) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "补录行程不能为空");
        }

        // 4. 补助非空校验
        if (reimbursementMapper.countSubsidyByReimbursementId(id) == 0) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "补助信息不能为空");
        }

        // 5. 行程日期重叠校验 → 50031
        int overlapCount = reimbursementMapper.countTripDateOverlap(id);
        if (overlapCount > 0) {
            throw new BusinessException(StatusCode.DATE_OVERLAP, "存在行程日期重叠，请调整出行日期");
        }

        // 6. 补助金额超标校验 → 50021
        int exceedCount = reimbursementMapper.countSubsidyAmountExceed(id);
        if (exceedCount > 0) {
            throw new BusinessException(StatusCode.AMOUNT_EXCEED_STANDARD, "补助金额超过标准，请调整补助金额");
        }

        // 7. 计算并更新补助总金额（关键修复）
        BigDecimal totalSubsidy = calculateTotalSubsidy(id);
        if (totalSubsidy.compareTo(existing.getSubsidyAmount()) != 0) {
            existing.setSubsidyAmount(totalSubsidy);
            reimbursementMapper.updateById(existing);
        }

        // 8. 校验通过：更新状态
        int rows = reimbursementMapper.submitDraft(id, dto.getVersion());
        if (rows == 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "提交失败：数据已被他人修改或状态已变更");
        }

        auditLogService.record(OperationType.REIMB_SUBMIT, String.valueOf(id), existing, null);

        redisCacheService.evictReimbListCache();
    }

    /**
     * 计算报销单的补助总金额
     * 从 subsidy_calendar 表汇总所有补助明细
     */
    private BigDecimal calculateTotalSubsidy(Long reimbursementId) {
        List<SubsidySummaryVO> summaryList = reimbursementMapper.selectSubsidySummaryByReimbursementId(reimbursementId);
        if (summaryList == null || summaryList.isEmpty()) {
            return BigDecimal.ZERO.setScale(2);
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (SubsidySummaryVO summary : summaryList) {
            if (summary.getSubsidyAmount() != null) {
                total = total.add(summary.getSubsidyAmount());
            }
        }
        return total.setScale(2);
    }

    // ==================== 撤回报销单 ====================

    @Override
    @Transactional
    public void withdrawReimbursement(String reimbursementId) {
        Long id = Long.parseLong(reimbursementId);
        Reimbursement existing = this.getById(id);

        // 1. 校验存在性
        if (existing == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销单不存在");
        }

        // 2. 校验状态：仅审批中可撤回
        if (existing.getStatus() != 1) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅审批中状态可撤回");
        }

        // 3. 执行撤回：1 → 0
        int rows = reimbursementMapper.withdraw(id);
        if (rows == 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "撤回失败：状态已变更");
        }

        auditLogService.record(OperationType.REIMB_WITHDRAW, String.valueOf(id), existing, null);

        redisCacheService.evictReimbListCache();
    }

    // ==================== 作废报销单 ====================

    @Override
    @Transactional
    public void invalidateReimbursement(String reimbursementId) {
        Long id = Long.parseLong(reimbursementId);
        Reimbursement existing = this.getById(id);

        // 1. 校验存在性
        if (existing == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销单不存在");
        }

        // 2. 校验状态：仅非终态（0/1/2）可作废
        int status = existing.getStatus();
        if (status != 0 && status != 1 && status != 2) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "当前状态不可作废");
        }

        // 3. 执行作废 → 状态 4
        int rows = reimbursementMapper.invalidate(id);
        if (rows == 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "作废失败：状态已变更");
        }

        auditLogService.record(OperationType.REIMB_INVALIDATE, String.valueOf(id), existing, null);

        redisCacheService.evictReimbListCache();
    }

    // ==================== 删除报销单 ====================

    @Override
    @Transactional
    public void deleteReimbursement(String reimbursementId) {
        // 参数校验
        if (!StringUtils.hasText(reimbursementId) || "undefined".equals(reimbursementId)) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销单ID不能为空");
        }
        
        Long id;
        try {
            id = Long.parseLong(reimbursementId);
        } catch (NumberFormatException e) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销单ID格式错误");
        }
        
        Reimbursement existing = this.getById(id);

        // 1. 校验存在性
        if (existing == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销单不存在");
        }

        // 2. 校验状态：草稿（0）和已作废（4）可删除
        int status = existing.getStatus();
        if (status != 0 && status != 4) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅草稿或已作废状态可删除");
        }

        // 3. 逻辑删除：deleted 0 → 1
        int rows = reimbursementMapper.logicalDeleteById(id);
        if (rows == 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "删除失败：状态已变更或已删除");
        }

        auditLogService.record(OperationType.REIMB_DELETE, String.valueOf(id), existing, null);

        redisCacheService.evictReimbListCache();
    }

    private void validateDraftRequired(ReimbursementSaveDTO dto) {
        if (StringUtils.hasText(dto.getTitle()) && dto.getTitle().length() > 500) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销标题不能超过500字");
        }
        if (!StringUtils.hasText(dto.getCompanyId())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "费用归属公司不能为空");
        }
        if (!StringUtils.hasText(dto.getDepartmentId())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "报销部门不能为空");
        }
        if (!StringUtils.hasText(dto.getReimburserId())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "报销人不能为空");
        }
        if (!StringUtils.hasText(dto.getBusinessTypeId())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "业务类型不能为空");
        }
    }

    private void validateRequiredFields(Reimbursement r) {
        if (StringUtils.hasText(r.getTitle()) && r.getTitle().length() > 500) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销标题不能超过500字");
        }
        if (!StringUtils.hasText(r.getCompanyId())
                || !StringUtils.hasText(r.getCompanyName())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "费用归属公司信息不完整");
        }
        if (!StringUtils.hasText(r.getDepartmentId())
                || !StringUtils.hasText(r.getDepartmentName())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "报销部门信息不完整");
        }
        if (!StringUtils.hasText(r.getReimburserId())
                || !StringUtils.hasText(r.getReimburserName())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "报销人信息不完整");
        }
        if (!StringUtils.hasText(r.getBusinessTypeId())
                || !StringUtils.hasText(r.getBusinessTypeName())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "业务类型信息不完整");
        }
    }
}
