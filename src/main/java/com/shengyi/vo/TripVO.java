package com.shengyi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TripVO {
    /**
     * 行程ID
     */
    private String tripId;

    /**
     * 出行人ID
     */
    private String travelerId;

    /**
     * 出行人姓名
     */
    private String travelerName;

    /**
     * 出发城市
     */
    private String departureCity;

    /**
     * 到达城市
     */
    private String arrivalCity;

    /**
     * 出发日期
     * 格式：yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    /**
     * 到达日期
     * 格式：yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    /**
     * 行程说明（限500字）
     */
    private String description;
}

/**
 * 补助信息汇总VO
 * 对应接口文档 2.2 中 SubsidySummaryVO
 */

