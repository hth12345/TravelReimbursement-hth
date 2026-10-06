package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("subsidy_calendar_history")
public class SubsidyCalendarHistory {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("reimbursement_id")
    private Long reimbursementId;

    @TableField("trip_id")
    private Long tripId;

    @TableField("calendar_date")
    private LocalDate calendarDate;

    @TableField("meal_standard")
    private BigDecimal mealStandard;

    @TableField("meal_amount")
    private BigDecimal mealAmount;

    @TableField("meal_checked")
    private Integer mealChecked;

    @TableField("traffic_standard")
    private BigDecimal trafficStandard;

    @TableField("traffic_amount")
    private BigDecimal trafficAmount;

    @TableField("traffic_checked")
    private Integer trafficChecked;

    @TableField("comm_standard")
    private BigDecimal commStandard;

    @TableField("comm_amount")
    private BigDecimal commAmount;

    @TableField("comm_checked")
    private Integer commChecked;

    @TableField(value = "archive_time", fill = FieldFill.INSERT)
    private LocalDateTime archiveTime;
}
