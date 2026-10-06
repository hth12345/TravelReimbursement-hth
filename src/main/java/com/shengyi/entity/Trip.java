package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 补录行程表实体类
 */
@Data
@TableName("trip")
public class Trip {

    /**
     * 主键ID（自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 报销单ID（外键关联reimbursement.id）
     */
    @TableField("reimbursement_id")
    private Long reimbursementId;

    /**
     * 出行人ID
     */
    @TableField("traveler_id")
    private String travelerId;

    /**
     * 出行人姓名
     */
    @TableField("traveler_name")
    private String travelerName;

    /**
     * 出发城市
     */
    @TableField("departure_city")
    private String departureCity;

    /**
     * 到达城市
     */
    @TableField("arrival_city")
    private String arrivalCity;

    /**
     * 出发日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 到达日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 行程说明
     */
    @TableField("description")
    private String description;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
