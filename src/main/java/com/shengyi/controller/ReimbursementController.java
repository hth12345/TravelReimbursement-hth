package com.shengyi.controller;

import com.shengyi.dto.ReimbursementListDTO;
import com.shengyi.dto.ReimbursementSaveDTO;
import com.shengyi.service.ReimbursementService;
import com.shengyi.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报销单控制器
 */
@RestController
@RequestMapping("/api/reimbursement")
public class ReimbursementController {

    @Autowired
    private ReimbursementService reimbursementService;

    /**
     * 查询报销单列表（支持分页和条件筛选）
     * @param query 查询条件对象，包含分页参数和筛选条件
     * @return 分页的报销单列表结果
     */
    @PostMapping("/list")
    public Result<PageResult<ReimbursementListVO>> getReimbursementList(@RequestBody ReimbursementListDTO query) {
        PageResult<ReimbursementListVO> pageResult = reimbursementService.getReimbursementList(query);
        return Result.success(pageResult);
    }

    /**
     * 获取报销单详情
     * @param reimbursementId 报销单ID
     * @return 报销单详细信息结果
     */
    @GetMapping("/detail/{reimbursementId}")
    public Result<ReimbursementDetailVO> getReimbursementDetail(@PathVariable String reimbursementId) {
        ReimbursementDetailVO detailVO = reimbursementService.getReimbursementDetail(reimbursementId);
        return Result.success(detailVO);
    }

    /**
     * 保存报销单草稿
     * @param dto 报销单数据传输对象
     * @return 新创建的报销单ID
     */
    @PostMapping("/saveDraft")
    public Result<Long> saveDraft(@RequestBody ReimbursementSaveDTO dto) {
        Long reimbursementId = reimbursementService.saveDraft(dto);
        return Result.success(reimbursementId);
    }

    /**
     * 提交报销单
     * @param dto 报销单数据传输对象
     * @return 操作结果
     */
    @PostMapping("/submit")
    public Result<Void> submitReimbursement(@RequestBody ReimbursementSaveDTO dto) {
        reimbursementService.submitReimbursement(dto);
        return Result.success(null);
    }

    /**
     * 撤回已提交的报销单
     * @param reimbursementId 报销单ID
     * @return 操作结果
     */
    @PostMapping("/withdraw/{reimbursementId}")
    public Result<Void> withdrawReimbursement(@PathVariable String reimbursementId) {
        reimbursementService.withdrawReimbursement(reimbursementId);
        return Result.success(null);
    }

    /**
     * 作废报销单
     * @param reimbursementId 报销单ID
     * @return 操作结果
     */
    @PostMapping("/invalidate/{reimbursementId}")
    public Result<Void> invalidateReimbursement(@PathVariable String reimbursementId) {
        reimbursementService.invalidateReimbursement(reimbursementId);
        return Result.success(null);
    }

    /**
     * 删除报销单
     * @param reimbursementId 报销单ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{reimbursementId}")
    public Result<Void> deleteReimbursement(@PathVariable String reimbursementId) {
        reimbursementService.deleteReimbursement(reimbursementId);
        return Result.success(null);
    }
}
