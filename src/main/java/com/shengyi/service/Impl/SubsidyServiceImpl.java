package com.shengyi.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shengyi.config.OperationType;
import com.shengyi.config.RedisCacheService;
import com.shengyi.dto.SubsidyItemSaveDTO;
import com.shengyi.service.AuditLogService;
import com.shengyi.dto.SubsidySaveDTO;
import com.shengyi.entity.CityTypeConfig;
import com.shengyi.entity.Reimbursement;
import com.shengyi.entity.SubsidyCalendar;
import com.shengyi.entity.Trip;
import com.shengyi.exception.BusinessException;
import com.shengyi.exception.StatusCode;
import com.shengyi.mapper.CityTypeConfigMapper;
import com.shengyi.mapper.ReimbursementMapper;
import com.shengyi.mapper.TripMapper;
import com.shengyi.service.SubsidyCalendarService;
import com.shengyi.service.SubsidyService;
import com.shengyi.vo.CalendarDayVO;
import com.shengyi.vo.SubsidyCalendarVO;
import com.shengyi.vo.SubsidyItemVO;
import com.shengyi.vo.SubsidySummaryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SubsidyServiceImpl implements SubsidyService {

    @Autowired
    private ReimbursementMapper reimbursementMapper;
    @Autowired
    private TripMapper tripMapper;
    @Autowired
    private CityTypeConfigMapper cityTypeConfigMapper;
    @Autowired
    private SubsidyCalendarService subsidyCalendarService;
    @Autowired
    private RedisCacheService redisCacheService;
    @Autowired
    private AuditLogService auditLogService;

    @Override
    public List<SubsidySummaryVO> GET_SUBSIDY_SUMMARY(String reimbursementId) {
        // 1. 查缓存
        List<SubsidySummaryVO> cached = redisCacheService.getSubsidySummary(reimbursementId);
        if (cached != null) {
            return cached;
        }

        // 2. 缓存未命中 → 查 DB
        Long id = Long.parseLong(reimbursementId);
        List<SubsidySummaryVO> list = reimbursementMapper.GET_SUBSIDY_SUMMARY_BY_REIMBURSEMENT_ID(id);

        // 3. 写入缓存（30 min）
        redisCacheService.setSubsidySummary(reimbursementId, list);
        return list;
    }

    @Override
    public SubsidyCalendarVO GET_SUBSIDY_CALENDAR(String tripId) {
        Long tid = Long.parseLong(tripId);

        Trip trip = tripMapper.selectById(tid);
        if (trip == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "行程不存在");
        }

        Reimbursement reimbursement = reimbursementMapper.selectById(trip.getReimbursementId());

        List<SubsidyCalendar> list = subsidyCalendarService.list(
                new LambdaQueryWrapper<SubsidyCalendar>()
                        .eq(SubsidyCalendar::getTripId, tid)
                        .orderByAsc(SubsidyCalendar::getCalendarDate));

        SubsidyCalendarVO vo = new SubsidyCalendarVO();
        vo.setTripId(tripId);
        vo.setStartDate(java.sql.Date.valueOf(trip.getStartDate()));
        vo.setEndDate(java.sql.Date.valueOf(trip.getEndDate()));
        vo.setDepartureCity(trip.getDepartureCity());
        vo.setArrivalCity(trip.getArrivalCity());
        vo.setCityType(getCityType(trip.getArrivalCity()));
        vo.setBusinessTypeName(reimbursement != null ? reimbursement.getBusinessTypeName() : null);

        BigDecimal totalApply = BigDecimal.ZERO.setScale(2);
        BigDecimal totalStandard = BigDecimal.ZERO.setScale(2);
        List<CalendarDayVO> calendarDays = new ArrayList<>();

        for (SubsidyCalendar sc : list) {
            CalendarDayVO day = new CalendarDayVO();
            day.setDate(java.sql.Date.valueOf(sc.getCalendarDate()));
            day.setWeekday(sc.getCalendarDate().getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, Locale.CHINESE));

            SubsidyItemVO meal = new SubsidyItemVO();
            meal.setStandard(sc.getMealStandard());
            meal.setAmount(sc.getMealAmount());
            meal.setChecked(sc.getMealChecked());
            day.setMeal(meal);
            if (sc.getMealChecked() != null && sc.getMealChecked() == 1) {
                totalApply = totalApply.add(sc.getMealAmount() != null ? sc.getMealAmount() : BigDecimal.ZERO);
                totalStandard = totalStandard.add(sc.getMealStandard() != null ? sc.getMealStandard() : BigDecimal.ZERO);
            }

            SubsidyItemVO traffic = new SubsidyItemVO();
            traffic.setStandard(sc.getTrafficStandard());
            traffic.setAmount(sc.getTrafficAmount());
            traffic.setChecked(sc.getTrafficChecked());
            day.setTraffic(traffic);
            if (sc.getTrafficChecked() != null && sc.getTrafficChecked() == 1) {
                totalApply = totalApply.add(sc.getTrafficAmount() != null ? sc.getTrafficAmount() : BigDecimal.ZERO);
                totalStandard = totalStandard.add(sc.getTrafficStandard() != null ? sc.getTrafficStandard() : BigDecimal.ZERO);
            }

            SubsidyItemVO comm = new SubsidyItemVO();
            comm.setStandard(sc.getCommStandard());
            comm.setAmount(sc.getCommAmount());
            comm.setChecked(sc.getCommChecked());
            day.setComm(comm);
            if (sc.getCommChecked() != null && sc.getCommChecked() == 1) {
                totalApply = totalApply.add(sc.getCommAmount() != null ? sc.getCommAmount() : BigDecimal.ZERO);
                totalStandard = totalStandard.add(sc.getCommStandard() != null ? sc.getCommStandard() : BigDecimal.ZERO);
            }

            calendarDays.add(day);
        }

        vo.setCalendarDays(calendarDays);
        vo.setTotalApplyAmount(totalApply);
        vo.setTotalStandardAmount(totalStandard);
        return vo;
    }

    @Override
    @Transactional
    public void POST_SUBSIDY_SAVE(SubsidySaveDTO dto) {
        Long tid = Long.parseLong(dto.getTripId());

        Trip trip = tripMapper.selectById(tid);
        if (trip == null) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "行程不存在");
        }

        Reimbursement reimbursement = reimbursementMapper.selectById(trip.getReimbursementId());
        if (reimbursement == null || reimbursement.getStatus() != 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅草稿状态可编辑补助");
        }

        List<SubsidyCalendar> existingList = subsidyCalendarService.list(
                new LambdaQueryWrapper<SubsidyCalendar>()
                        .eq(SubsidyCalendar::getTripId, tid));
        java.util.Map<LocalDate, SubsidyCalendar> map = new java.util.HashMap<>();
        for (SubsidyCalendar sc : existingList) {
            map.put(sc.getCalendarDate(), sc);
        }

        // 保存修改前的快照，用于异动日志
        List<SubsidyCalendar> preSnapshot = existingList.stream().map(sc -> {
            SubsidyCalendar copy = new SubsidyCalendar();
            BeanUtils.copyProperties(sc, copy);
            return copy;
        }).collect(Collectors.toList());

        BigDecimal totalSubsidy = BigDecimal.ZERO.setScale(2);
        if (dto.getItems() != null) {
            for (SubsidyItemSaveDTO item : dto.getItems()) {
                LocalDate itemDate = new java.sql.Date(item.getDate().getTime()).toLocalDate();
                // 校验日期是否在行程范围内（而非依赖 subsidy_calendar 表已有记录）
                if (itemDate.isBefore(trip.getStartDate()) || itemDate.isAfter(trip.getEndDate())) {
                    throw new BusinessException(StatusCode.DATE_OVERLAP,
                            itemDate + " 不在该行程的日期范围内");
                }
                SubsidyCalendar sc = map.get(itemDate);
                if (sc == null) {
                    throw new BusinessException(StatusCode.SYSTEM_ERROR,
                            "补助日历数据异常，日期 " + itemDate + " 的记录不存在");
                }

                validateAmount(item.getMealAmount(), sc.getMealStandard(), itemDate, "餐补");
                validateAmount(item.getTrafficAmount(), sc.getTrafficStandard(), itemDate, "交通补助");
                validateAmount(item.getCommAmount(), sc.getCommStandard(), itemDate, "通讯补助");

                sc.setMealAmount(item.getMealAmount());
                sc.setMealChecked(item.getMealChecked());
                sc.setTrafficAmount(item.getTrafficAmount());
                sc.setTrafficChecked(item.getTrafficChecked());
                sc.setCommAmount(item.getCommAmount());
                sc.setCommChecked(item.getCommChecked());

                if (item.getMealChecked() != null && item.getMealChecked() == 1
                        && item.getMealAmount() != null) {
                    totalSubsidy = totalSubsidy.add(item.getMealAmount());
                }
                if (item.getTrafficChecked() != null && item.getTrafficChecked() == 1
                        && item.getTrafficAmount() != null) {
                    totalSubsidy = totalSubsidy.add(item.getTrafficAmount());
                }
                if (item.getCommChecked() != null && item.getCommChecked() == 1
                        && item.getCommAmount() != null) {
                    totalSubsidy = totalSubsidy.add(item.getCommAmount());
                }
            }
        }

        if (dto.getTotalAmount() != null && totalSubsidy.compareTo(dto.getTotalAmount()) != 0) {
            throw new BusinessException(StatusCode.AMOUNT_MISMATCH, "数据异常，请刷新后重新编辑");
        }

        subsidyCalendarService.updateBatchById(existingList);

        // 记录异动日志
        auditLogService.record(OperationType.SUBSIDY_SAVE,
                String.valueOf(trip.getReimbursementId()), preSnapshot, existingList);

        reimbursement.setSubsidyAmount(totalSubsidy);
        reimbursementMapper.updateById(reimbursement);

        // 清除补助汇总缓存（key = reimbursementId）
        redisCacheService.evictSubsidySummary(String.valueOf(trip.getReimbursementId()));
        // 同时清除列表缓存（补助金额变化影响列表显示）
        redisCacheService.evictReimbListCache();
    }

    private void validateAmount(BigDecimal amount, BigDecimal standard, LocalDate date, String type) {
        if (amount != null && standard != null && amount.compareTo(standard) > 0) {
            throw new BusinessException(StatusCode.AMOUNT_EXCEED_STANDARD,
                    date + " " + type + "金额超过标准");
        }
    }

    private String getCityType(String cityName) {
        if (cityName == null) return "二线";
        CityTypeConfig config = cityTypeConfigMapper.selectOne(
                new LambdaQueryWrapper<CityTypeConfig>()
                        .eq(CityTypeConfig::getCityName, cityName));
        if (config == null) return "二线";
        switch (config.getCityType()) {
            case 1: return "一线";
            case 3: return "三线";
            default: return "二线";
        }
    }
}
