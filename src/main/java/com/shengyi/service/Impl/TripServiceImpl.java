package com.shengyi.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shengyi.config.OperationType;
import com.shengyi.config.RedisCacheService;
import com.shengyi.dto.TripCopyDTO;
import com.shengyi.service.AuditLogService;
import com.shengyi.dto.TripSaveDTO;
import com.shengyi.entity.Employee;
import com.shengyi.entity.Reimbursement;
import com.shengyi.entity.Trip;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shengyi.exception.BusinessException;
import com.shengyi.vo.TripVO;
import com.shengyi.exception.StatusCode;
import com.shengyi.mapper.ReimbursementMapper;
import com.shengyi.mapper.TripMapper;
import com.shengyi.service.EmployeeService;
import com.shengyi.service.SubsidyCalendarService;
import com.shengyi.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class TripServiceImpl extends ServiceImpl<TripMapper, Trip> implements TripService {

    @Autowired
    private TripMapper tripMapper;
    @Autowired
    private ReimbursementMapper reimbursementMapper;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SubsidyCalendarService subsidyCalendarService;
    @Autowired
    private RedisCacheService redisCacheService;
    @Autowired
    private AuditLogService auditLogService;

    @Override
    @Transactional
    public String POST_TRIP_SAVE(TripSaveDTO dto) {
        validateTripRequired(dto);

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException(StatusCode.DATE_OVERLAP, "到达日期不能早于出发日期");
        }
        if (dto.getEndDate().isAfter(LocalDate.now())) {
            throw new BusinessException(StatusCode.DATE_OVERLAP, "到达日期不能晚于当前日期");
        }

        Reimbursement reimbursement = reimbursementMapper.selectById(dto.getReimbursementId());
        if (reimbursement == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销单不存在");
        }
        if (reimbursement.getStatus() != 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅草稿状态可编辑行程");
        }

        boolean isUpdate = StringUtils.hasText(dto.getTripId());
        Trip oldTrip = null;
        if (isUpdate) {
            Long tripId = Long.parseLong(dto.getTripId());
            oldTrip = this.getById(tripId);
            int overlapCount = tripMapper.COUNT_DATE_OVERLAP_EXCLUDE_SELF(
                    dto.getReimbursementId(), dto.getTravelerId(), tripId,
                    dto.getStartDate(), dto.getEndDate());
            if (overlapCount > 0) {
                throw new BusinessException(StatusCode.DATE_OVERLAP, "该出行人在此时段已有其他行程");
            }
        } else {
            int overlapCount = tripMapper.COUNT_DATE_OVERLAP(
                    dto.getReimbursementId(), dto.getTravelerId(),
                    dto.getStartDate(), dto.getEndDate());
            if (overlapCount > 0) {
                throw new BusinessException(StatusCode.DATE_OVERLAP, "该出行人在此时段已有其他行程");
            }
        }

        Trip trip = buildTrip(dto, isUpdate);
        if (isUpdate) {
            subsidyCalendarService.DELETE_BY_TRIP_ID(Long.parseLong(dto.getTripId()));
            this.updateById(trip);
            auditLogService.record(OperationType.TRIP_UPDATE, String.valueOf(trip.getId()), oldTrip, trip);
        } else {
            this.save(trip);
            System.out.println("=== 行程保存成功，ID: " + trip.getId() + " ===");
            auditLogService.record(OperationType.TRIP_CREATE, String.valueOf(trip.getId()), null, trip);
        }

        System.out.println("=== 准备生成补助日历 ===");
        subsidyCalendarService.GENERATE_CALENDAR(
                trip.getReimbursementId(), trip.getId(),
                trip.getStartDate(), trip.getEndDate(), trip.getArrivalCity());

        // 清除补助汇总缓存 + 列表缓存
        redisCacheService.evictSubsidySummary(String.valueOf(trip.getReimbursementId()));
        redisCacheService.evictReimbListCache();

        return String.valueOf(trip.getId());
    }

    @Override
    @Transactional
    public void DELETE_TRIP(String tripId) {
        Long id = Long.parseLong(tripId);
        Trip trip = this.getById(id);
        if (trip == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "行程不存在");
        }

        Reimbursement reimbursement = reimbursementMapper.selectById(trip.getReimbursementId());
        if (reimbursement == null || reimbursement.getStatus() != 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅草稿状态可删除行程");
        }

        subsidyCalendarService.DELETE_BY_TRIP_ID(id);
        this.removeById(id);

        auditLogService.record(OperationType.TRIP_DELETE, String.valueOf(id), trip, null);

        // 清除补助汇总缓存 + 列表缓存
        redisCacheService.evictSubsidySummary(String.valueOf(trip.getReimbursementId()));
        redisCacheService.evictReimbListCache();
    }

    @Override
    @Transactional
    public TripVO POST_TRIP_COPY(TripCopyDTO dto) {
        Long srcId = Long.parseLong(dto.getSourceTripId());
        Long targetId = Long.parseLong(dto.getTargetReimbursementId());

        Trip source = this.getById(srcId);
        if (source == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "源行程不存在");
        }

        Reimbursement target = reimbursementMapper.selectById(targetId);
        if (target == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "目标报销单不存在");
        }
        if (target.getStatus() != 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅草稿状态可新增行程");
        }

        int overlapCount = tripMapper.COUNT_DATE_OVERLAP(
                targetId, source.getTravelerId(),
                source.getStartDate(), source.getEndDate());
        if (overlapCount > 0) {
            throw new BusinessException(StatusCode.DATE_OVERLAP, "该出行人在目标报销单此时段已有其他行程");
        }

        Trip newTrip = new Trip();
        newTrip.setReimbursementId(targetId);
        newTrip.setTravelerId(source.getTravelerId());
        newTrip.setTravelerName(source.getTravelerName());
        newTrip.setDepartureCity(source.getDepartureCity());
        newTrip.setArrivalCity(source.getArrivalCity());
        newTrip.setStartDate(source.getStartDate());
        newTrip.setEndDate(source.getEndDate());
        newTrip.setDescription(source.getDescription());
        this.save(newTrip);

        auditLogService.record(OperationType.TRIP_COPY, String.valueOf(newTrip.getId()), source, newTrip);

        subsidyCalendarService.GENERATE_CALENDAR(
                newTrip.getReimbursementId(), newTrip.getId(),
                newTrip.getStartDate(), newTrip.getEndDate(), newTrip.getArrivalCity());

        // 清除补助汇总缓存 + 列表缓存
        redisCacheService.evictSubsidySummary(String.valueOf(newTrip.getReimbursementId()));
        redisCacheService.evictReimbListCache();

        return toTripVO(newTrip);
    }

    private TripVO toTripVO(Trip trip) {
        TripVO vo = new TripVO();
        vo.setTripId(String.valueOf(trip.getId()));
        vo.setTravelerId(trip.getTravelerId());
        vo.setTravelerName(trip.getTravelerName());
        vo.setDepartureCity(trip.getDepartureCity());
        vo.setArrivalCity(trip.getArrivalCity());
        vo.setStartDate(java.sql.Date.valueOf(trip.getStartDate()));
        vo.setEndDate(java.sql.Date.valueOf(trip.getEndDate()));
        vo.setDescription(trip.getDescription());
        return vo;
    }

    private void validateTripRequired(TripSaveDTO dto) {
        if (dto.getReimbursementId() == null) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "报销单ID不能为空");
        }
        if (!StringUtils.hasText(dto.getTravelerId())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "出行人不能为空");
        }
        if (!StringUtils.hasText(dto.getDepartureCity())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "出发城市不能为空");
        }
        if (!StringUtils.hasText(dto.getArrivalCity())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "到达城市不能为空");
        }
        if (dto.getStartDate() == null) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "出发日期不能为空");
        }
        if (dto.getEndDate() == null) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "到达日期不能为空");
        }
    }

    private Trip buildTrip(TripSaveDTO dto, boolean isUpdate) {
        Trip trip = new Trip();
        if (isUpdate) {
            trip.setId(Long.parseLong(dto.getTripId()));
        }
        trip.setReimbursementId(dto.getReimbursementId());
        trip.setTravelerId(dto.getTravelerId());
        trip.setTravelerName(lookupTravelerName(dto.getTravelerId()));
        trip.setDepartureCity(dto.getDepartureCity());
        trip.setArrivalCity(dto.getArrivalCity());
        trip.setStartDate(dto.getStartDate());
        trip.setEndDate(dto.getEndDate());
        trip.setDescription(dto.getDescription());
        return trip;
    }

    private String lookupTravelerName(String travelerId) {
        Employee emp = employeeService.getOne(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getReimburserId, travelerId));
        return emp != null ? emp.getReimburserName() : null;
    }
}
