package com.shengyi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shengyi.entity.*;
import com.shengyi.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据归档服务 — 将 6 个月前已完成/作废的报销单迁移至历史表
 */
@Service
public class ArchiveService {

    @Autowired
    private ReimbursementMapper reimbursementMapper;
    @Autowired
    private TripMapper tripMapper;
    @Autowired
    private SubsidyCalendarMapper subsidyCalendarMapper;
    @Autowired
    private ReimbursementHistoryMapper reimbursementHistoryMapper;
    @Autowired
    private TripHistoryMapper tripHistoryMapper;
    @Autowired
    private SubsidyCalendarHistoryMapper subsidyCalendarHistoryMapper;

    /**
     * 定时归档任务：每天凌晨 3 点执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void scheduledArchive() {
        archive(LocalDateTime.now().minusMonths(6));
    }

    /**
     * 归档指定时间之前的已完成 / 作废报销单
     * @param cutoff 截止时间（update_time < cutoff）
     * @return 归档数量
     */
    @Transactional
    public int archive(LocalDateTime cutoff) {
        List<Reimbursement> list = reimbursementMapper.selectList(
                new LambdaQueryWrapper<Reimbursement>()
                        .in(Reimbursement::getStatus, 2, 3, 4)  // 审批通过/已完成/已作废
                        .lt(Reimbursement::getUpdateTime, cutoff)
        );

        if (list.isEmpty()) {
            return 0;
        }

        List<Long> ids = list.stream().map(Reimbursement::getId).collect(Collectors.toList());

        // 1. 归档行程 → trip_history
        List<Trip> trips = tripMapper.selectList(
                new LambdaQueryWrapper<Trip>().in(Trip::getReimbursementId, ids));
        for (Trip t : trips) {
            tripHistoryMapper.insert(toTripHistory(t));
        }

        // 2. 归档补助日历 → subsidy_calendar_history
        List<SubsidyCalendar> calendars = subsidyCalendarMapper.selectList(
                new LambdaQueryWrapper<SubsidyCalendar>().in(SubsidyCalendar::getReimbursementId, ids));
        for (SubsidyCalendar sc : calendars) {
            subsidyCalendarHistoryMapper.insert(toSubsidyCalendarHistory(sc));
        }

        // 3. 归档报销单 → reimbursement_history
        for (Reimbursement r : list) {
            reimbursementHistoryMapper.insert(toReimbursementHistory(r));
        }

        // 4. 删除主表（CASCADE 自动删除 trip 和 subsidy_calendar）
        // 由于外键约束，先删日历再删行程再删报销单
        if (!calendars.isEmpty()) {
            subsidyCalendarMapper.delete(
                    new LambdaQueryWrapper<SubsidyCalendar>().in(SubsidyCalendar::getReimbursementId, ids));
        }
        if (!trips.isEmpty()) {
            tripMapper.delete(
                    new LambdaQueryWrapper<Trip>().in(Trip::getReimbursementId, ids));
        }
        reimbursementMapper.deleteBatchIds(ids);

        return list.size();
    }

    private ReimbursementHistory toReimbursementHistory(Reimbursement r) {
        ReimbursementHistory h = new ReimbursementHistory();
        BeanUtils.copyProperties(r, h);
        h.setArchiveTime(LocalDateTime.now());
        return h;
    }

    private TripHistory toTripHistory(Trip t) {
        TripHistory h = new TripHistory();
        BeanUtils.copyProperties(t, h);
        h.setArchiveTime(LocalDateTime.now());
        return h;
    }

    private SubsidyCalendarHistory toSubsidyCalendarHistory(SubsidyCalendar sc) {
        SubsidyCalendarHistory h = new SubsidyCalendarHistory();
        BeanUtils.copyProperties(sc, h);
        h.setArchiveTime(LocalDateTime.now());
        return h;
    }
}
