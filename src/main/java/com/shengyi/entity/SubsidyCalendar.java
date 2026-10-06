package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 补助日历表实体类
 */
@Data
@TableName("subsidy_calendar")
public class SubsidyCalendar {

    /**
     * 主键ID（自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 报销单ID（冗余字段，便于快速查询）
     */
    @TableField("reimbursement_id")
    private Long reimbursementId;

    /**
     * 外键，关联trip.id
     */
    @TableField("trip_id")
    private Long tripId;

    /**
     * 具体补助日期
     */
    @TableField("calendar_date")
    private LocalDate calendarDate;

    /**
     * 餐补标准（元/天）
     */
    @TableField("meal_standard")
    private BigDecimal mealStandard;

    /**
     * 实际餐补金额
     */
    @TableField("meal_amount")
    private BigDecimal mealAmount;

    /**
     * 餐补是否勾选：0-否，1-是
     */
    @TableField("meal_checked")
    private Integer mealChecked;

    /**
     * 交通补助标准（元/天）
     */
    @TableField("traffic_standard")
    private BigDecimal trafficStandard;

    /**
     * 实际交通补助金额
     */
    @TableField("traffic_amount")
    private BigDecimal trafficAmount;

    /**
     * 交通补助是否勾选：0-否，1-是
     */
    @TableField("traffic_checked")
    private Integer trafficChecked;

    /**
     * 通讯补助标准（元/天）
     */
    @TableField("comm_standard")
    private BigDecimal commStandard;

    /**
     * 实际通讯补助金额
     */
    @TableField("comm_amount")
    private BigDecimal commAmount;

    /**
     * 通讯补助是否勾选：0-否，1-是
     */
    @TableField("comm_checked")
    private Integer commChecked;

    /**
     * 逻辑删除标记：0-正常，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
