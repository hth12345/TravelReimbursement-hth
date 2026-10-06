package com.shengyi.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public  class SubsidySummaryVO {
    /**
     * 行程ID
     */
    private String tripId;

    /**
     * 出行人
     */
    private String travelerName;

    /**
     * 出差日期范围
     */
    private String travelDateRange;

    /**
     * 补助天数
     */
    private Integer subsidyDays;

    /**
     * 行程（如"武汉→北京"）
     */
    private String route;

    /**
     * 补助城市
     */
    private String subsidyCity;

    /**
     * 申请金额
     */
    private BigDecimal applyAmount;

    /**
     * 补助金额
     */
    private BigDecimal subsidyAmount;

    /**
     * 餐费补助金额
     */
    private BigDecimal mealSubsidyAmount;

    /**
     * 交通补助金额
     */
    private BigDecimal trafficSubsidyAmount;

    /**
     * 通讯补助金额
     */
    private BigDecimal commSubsidyAmount;
}


