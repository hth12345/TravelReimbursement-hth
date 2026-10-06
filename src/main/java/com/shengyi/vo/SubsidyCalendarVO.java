package com.shengyi.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 补助日历明细 VO
 */
@Data
public class SubsidyCalendarVO {

    /** 行程ID */
    private String tripId;

    /** 开始日期 */
    private Date startDate;

    /** 结束日期 */
    private Date endDate;

    /** 出发城市 */
    private String departureCity;

    /** 到达城市 */
    private String arrivalCity;

    /** 城市类型（一线/二线/三线） */
    private String cityType;

    /** 出差类型 */
    private String businessTypeName;

    /** 标准总额（已勾选项标准金额合计） */
    private BigDecimal totalStandardAmount;

    /** 补助总额（已勾选项实际金额合计） */
    private BigDecimal totalApplyAmount;

    /** 每日补助明细 */
    private List<CalendarDayVO> calendarDays;
}
