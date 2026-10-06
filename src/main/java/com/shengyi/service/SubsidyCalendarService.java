package com.shengyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shengyi.entity.SubsidyCalendar;

import java.time.LocalDate;

public interface SubsidyCalendarService extends IService<SubsidyCalendar> {

    void DELETE_BY_TRIP_ID(Long tripId);

    void SAVE_BATCH(java.util.List<SubsidyCalendar> list);

    void GENERATE_CALENDAR(Long reimbursementId, Long tripId,
                           LocalDate startDate, LocalDate endDate,
                           String arrivalCity);
}
