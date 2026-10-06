package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("trip_history")
public class TripHistory {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("reimbursement_id")
    private Long reimbursementId;

    @TableField("traveler_id")
    private String travelerId;

    @TableField("traveler_name")
    private String travelerName;

    @TableField("departure_city")
    private String departureCity;

    @TableField("arrival_city")
    private String arrivalCity;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("description")
    private String description;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField(value = "archive_time", fill = FieldFill.INSERT)
    private LocalDateTime archiveTime;
}
