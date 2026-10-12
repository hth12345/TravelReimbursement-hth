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
    public String saveTrip(TripSaveDTO dto) {
        validateTripRequired(dto);

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException(StatusCode.DATE_OVERLAP, "到达日期不能早于出发日期");
        }
        if (dto.getEndDate().isAfter(LocalDate.now())) {
            throw new BusinessException(StatusCode.DATE_OVERLAP, "到达日期不能晚于当前日期");
        }

        Reimbursement reimbursementRes = reimbursementMapper.selectById(dto.getReimbursementId());
        if (reimbursementRes == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销单不存在");
        }
        if (reimbursementRes.getStatus() != 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅草稿状态可编辑行程");
        }

        boolean hasTripId = StringUtils.hasText(dto.getTripId());
        Trip oldTrip = null;
        if (hasTripId) {
            Long tripId = Long.parseLong(dto.getTripId());
            oldTrip = this.getById(tripId);
            int overlapCount = tripMapper.countDateOverlapExcludeSelf(
                    dto.getReimbursementId(), dto.getTravelerId(), tripId,
                    dto.getStartDate(), dto.getEndDate());
            if (overlapCount > 0) {
                throw new BusinessException(StatusCode.DATE_OVERLAP, "该出行人在此时段已有其他行程");
            }
        } else {
            int overlapCount = tripMapper.countDateOverlap(
                    dto.getReimbursementId(), dto.getTravelerId(),
                    dto.getStartDate(), dto.getEndDate());
            if (overlapCount > 0) {
                throw new BusinessException(StatusCode.DATE_OVERLAP, "该出行人在此时段已有其他行程");
            }
        }

        Trip trip = buildTrip(dto, hasTripId);
        if (hasTripId) {
            subsidyCalendarService.deleteByTripId(Long.parseLong(dto.getTripId()));
            this.updateById(trip);
            auditLogService.record(OperationType.TRIP_UPDATE, String.valueOf(trip.getId()), oldTrip, trip);
        } else {
            this.save(trip);
            System.out.println("=== 行程保存成功，ID: " + trip.getId() + " ===");
            auditLogService.record(OperationType.TRIP_CREATE, String.valueOf(trip.getId()), null, trip);
        }

        System.out.println("=== 准备生成补助日历 ===");
        subsidyCalendarService.generateCalendar(
                trip.getReimbursementId(), trip.getId(),
                trip.getStartDate(), trip.getEndDate(), trip.getArrivalCity());

        // 清除补助汇总缓存 + 列表缓存
        redisCacheService.evictSubsidySummary(String.valueOf(trip.getReimbursementId()));
        redisCacheService.evictReimbListCache();

        return String.valueOf(trip.getId());
    }

    @Override
    @Transactional
    public void deleteTrip(String tripId) {
        Long id = Long.parseLong(tripId);
        Trip tripRes = this.getById(id);
        if (tripRes == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "行程不存在");
        }

        Reimbursement reimbursementRes = reimbursementMapper.selectById(tripRes.getReimbursementId());
        if (reimbursementRes == null || reimbursementRes.getStatus() != 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅草稿状态可删除行程");
        }

        subsidyCalendarService.deleteByTripId(id);
        this.removeById(id);

        auditLogService.record(OperationType.TRIP_DELETE, String.valueOf(id), tripRes, null);

        // 清除补助汇总缓存 + 列表缓存
        redisCacheService.evictSubsidySummary(String.valueOf(tripRes.getReimbursementId()));
        redisCacheService.evictReimbListCache();
    }

    @Override
    @Transactional
    public TripVO copyTrip(TripCopyDTO dto) {
        Long srcId = Long.parseLong(dto.getSourceTripId());
        Long targetId = Long.parseLong(dto.getTargetReimbursementId());

        Trip TripRes = this.getById(srcId);
        if (TripRes == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "源行程不存在");
        }

        Reimbursement targetReimbursement = reimbursementMapper.selectById(targetId);
        if (targetReimbursement == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "目标报销单不存在");
        }
        if (targetReimbursement.getStatus() != 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅草稿状态可新增行程");
        }

        int overlapCount = tripMapper.countDateOverlap(
                targetId, TripRes.getTravelerId(),
                TripRes.getStartDate(), TripRes.getEndDate());
        if (overlapCount > 0) {
            throw new BusinessException(StatusCode.DATE_OVERLAP, "该出行人在目标报销单此时段已有其他行程");
        }

        Trip newTrip = new Trip();
        newTrip.setReimbursementId(targetId);
        newTrip.setTravelerId(TripRes.getTravelerId());
        newTrip.setTravelerName(TripRes.getTravelerName());
        newTrip.setDepartureCity(TripRes.getDepartureCity());
        newTrip.setArrivalCity(TripRes.getArrivalCity());
        newTrip.setStartDate(TripRes.getStartDate());
        newTrip.setEndDate(TripRes.getEndDate());
        newTrip.setDescription(TripRes.getDescription());
        this.save(newTrip);

        auditLogService.record(OperationType.TRIP_COPY, String.valueOf(newTrip.getId()), TripRes, newTrip);

        subsidyCalendarService.generateCalendar(
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

    private Trip buildTrip(TripSaveDTO dto, boolean hasTripId) {
        Trip tripReq = new Trip();
        if (hasTripId) {
            tripReq.setId(Long.parseLong(dto.getTripId()));
        }
        tripReq.setReimbursementId(dto.getReimbursementId());
        tripReq.setTravelerId(dto.getTravelerId());
        tripReq.setTravelerName(lookupTravelerName(dto.getTravelerId()));
        tripReq.setDepartureCity(dto.getDepartureCity());
        tripReq.setArrivalCity(dto.getArrivalCity());
        tripReq.setStartDate(dto.getStartDate());
        tripReq.setEndDate(dto.getEndDate());
        tripReq.setDescription(dto.getDescription());
        return tripReq;
    }

    private String lookupTravelerName(String travelerId) {
        Employee emp = employeeService.getOne(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getReimburserId, travelerId));
        return emp != null ? emp.getReimburserName() : null;
    }
}
