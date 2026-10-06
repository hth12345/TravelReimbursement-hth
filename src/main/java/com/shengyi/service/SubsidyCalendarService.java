package com.shengyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shengyi.entity.SubsidyCalendar;

import java.time.LocalDate;

public interface SubsidyCalendarService extends IService<SubsidyCalendar> {

    void deleteByTripId(Long tripId);

    void generateCalendar(Long reimbursementId, Long tripId,
                           LocalDate startDate, LocalDate endDate,
                           String arrivalCity);
}
