package com.shengyi.vo;

import lombok.Data;
import java.util.Date;

/**
 * 单日补助明细 VO
 */
@Data
public class CalendarDayVO {

    /** 日期 */
    private Date date;

    /** 星期几 */
    private String weekday;

    /** 餐费补助 */
    private SubsidyItemVO meal;

    /** 交通补助 */
    private SubsidyItemVO traffic;

    /** 通讯补助 */
    private SubsidyItemVO comm;
}
