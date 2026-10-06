package com.shengyi.service;

import com.shengyi.dto.SubsidySaveDTO;
import com.shengyi.vo.SubsidyCalendarVO;
import com.shengyi.vo.SubsidySummaryVO;

import java.util.List;

public interface SubsidyService {

    List<SubsidySummaryVO> getSubsidySummary(String reimbursementId);

    SubsidyCalendarVO getSubsidyCalendar(String tripId);

    void saveSubsidy(SubsidySaveDTO dto);
}
