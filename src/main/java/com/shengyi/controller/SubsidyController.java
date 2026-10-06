package com.shengyi.controller;

import com.shengyi.dto.SubsidySaveDTO;
import com.shengyi.service.SubsidyService;
import com.shengyi.vo.Result;
import com.shengyi.vo.SubsidyCalendarVO;
import com.shengyi.vo.SubsidySummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 补助信息控制器
 */
@RestController
@RequestMapping("/api")
public class SubsidyController {

    @Autowired
    private SubsidyService subsidyService;

    /**
     * 获取补助信息汇总
     */
    @GetMapping("/reimbursement/subsidy/summary")
    public Result<List<SubsidySummaryVO>> getSubsidySummary(@RequestParam String reimbursementId) {
        List<SubsidySummaryVO> list = subsidyService.getSubsidySummary(reimbursementId);
        return Result.success(list);
    }

    /**
     * 获取补助日历明细
     */
    @GetMapping("/trip/subsidy/calendar")
    public Result<SubsidyCalendarVO> getSubsidyCalendar(@RequestParam String tripId) {
        SubsidyCalendarVO subsidyCalendarVO = subsidyService.getSubsidyCalendar(tripId);
        return Result.success(subsidyCalendarVO);
    }

    /**
     * 保存补助日历信息
     */
    @PostMapping("/trip/subsidy/save")
    public Result<Void> saveSubsidy(@RequestBody SubsidySaveDTO dto) {
        subsidyService.saveSubsidy(dto);
        return Result.success();
    }
}
