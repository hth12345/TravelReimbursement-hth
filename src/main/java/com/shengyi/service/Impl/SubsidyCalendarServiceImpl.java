package com.shengyi.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shengyi.entity.CityTypeConfig;
import com.shengyi.entity.SubsidyCalendar;
import com.shengyi.mapper.CityTypeConfigMapper;
import com.shengyi.mapper.SubsidyCalendarMapper;
import com.shengyi.service.SubsidyCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubsidyCalendarServiceImpl extends ServiceImpl<SubsidyCalendarMapper, SubsidyCalendar> implements SubsidyCalendarService {

    private static final BigDecimal MEAL_STANDARD_TIER1 = new BigDecimal("100");
    private static final BigDecimal MEAL_STANDARD_TIER2 = new BigDecimal("80");
    private static final BigDecimal MEAL_STANDARD_TIER3 = new BigDecimal("50");
    private static final BigDecimal DEFAULT_TRAFFIC_STANDARD = new BigDecimal("40");
    private static final BigDecimal DEFAULT_COMM_STANDARD = new BigDecimal("40");

    @Autowired
    private SubsidyCalendarMapper subsidyCalendarMapper;
    @Autowired
    private CityTypeConfigMapper cityTypeConfigMapper;

    @Override
    @Transactional
    public void deleteByTripId(Long tripId) {
        subsidyCalendarMapper.deleteByTripId(tripId);
    }

    @Async
    @Override
    @Transactional
    public void generateCalendar(Long reimbursementId, Long tripId,
                                  LocalDate startDate, LocalDate endDate,
                                  String arrivalCity) {
        System.out.println("=== GENERATE_CALENDAR 开始 ===");
        System.out.println("报销单ID: " + reimbursementId);
        System.out.println("行程ID: " + tripId);
        System.out.println("开始日期: " + startDate);
        System.out.println("结束日期: " + endDate);
        System.out.println("到达城市: " + arrivalCity);
        
        BigDecimal mealStandard = getMealStandard(arrivalCity);
        System.out.println("餐补标准: " + mealStandard);
        
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        System.out.println("补助天数: " + days);
        
        if (days <= 0) {
            System.out.println("错误: 日期范围无效");
            return;
        }
        
        List<SubsidyCalendar> list = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            SubsidyCalendar sc = new SubsidyCalendar();
            sc.setReimbursementId(reimbursementId);
            sc.setTripId(tripId);
            sc.setCalendarDate(date);
            sc.setMealStandard(mealStandard);
            sc.setMealAmount(mealStandard);
            sc.setMealChecked(1);
            sc.setTrafficStandard(DEFAULT_TRAFFIC_STANDARD);
            sc.setTrafficAmount(DEFAULT_TRAFFIC_STANDARD);
            sc.setTrafficChecked(1);
            sc.setCommStandard(DEFAULT_COMM_STANDARD);
            sc.setCommAmount(DEFAULT_COMM_STANDARD);
            sc.setCommChecked(1);
            sc.setDeleted(0);
            list.add(sc);
        }
        
        System.out.println("准备保存 " + list.size() + " 条补助日历记录");
        
        boolean result = this.saveBatch(list);
        System.out.println("保存结果: " + result);
        System.out.println("=== GENERATE_CALENDAR 结束 ===");
    }

    private BigDecimal getMealStandard(String cityName) {
        if (cityName == null) return MEAL_STANDARD_TIER2;
        CityTypeConfig config = cityTypeConfigMapper.selectOne(
                new LambdaQueryWrapper<CityTypeConfig>()
                        .eq(CityTypeConfig::getCityName, cityName));
        if (config == null) return MEAL_STANDARD_TIER2;
        switch (config.getCityType()) {
            case 1: return MEAL_STANDARD_TIER1;
            case 3: return MEAL_STANDARD_TIER3;
            default: return MEAL_STANDARD_TIER2;
        }
    }
}
