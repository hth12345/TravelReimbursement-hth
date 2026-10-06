package com.shengyi.service;

import com.shengyi.dto.SubsidySaveDTO;
import com.shengyi.vo.SubsidyCalendarVO;
import com.shengyi.vo.SubsidySummaryVO;

import java.util.List;

public interface SubsidyService {

    List<SubsidySummaryVO> GET_SUBSIDY_SUMMARY(String reimbursementId);

    SubsidyCalendarVO GET_SUBSIDY_CALENDAR(String tripId);

    void POST_SUBSIDY_SAVE(SubsidySaveDTO dto);
}
