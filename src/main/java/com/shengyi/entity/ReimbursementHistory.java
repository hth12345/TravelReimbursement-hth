package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报销单历史归档表实体
 */
@Data
@TableName("reimbursement_history")
public class ReimbursementHistory {

    /** 保留原主键 ID */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("reimbursement_no")
    private String reimbursementNo;

    @TableField("title")
    private String title;

    @TableField("reason")
    private String reason;

    @TableField("status")
    private Integer status;

    @TableField("version")
    private Integer version;

    @TableField("reimburser_id")
    private String reimburserId;

    @TableField("reimburser_no")
    private String reimburserNo;

    @TableField("reimburser_name")
    private String reimburserName;

    @TableField("department_id")
    private String departmentId;

    @TableField("department_no")
    private String departmentNo;

    @TableField("department_name")
    private String departmentName;

    @TableField("company_id")
    private String companyId;

    @TableField("company_no")
    private String companyNo;

    @TableField("company_name")
    private String companyName;

    @TableField("business_type_id")
    private String businessTypeId;

    @TableField("business_type_name")
    private String businessTypeName;

    @TableField("subsidy_amount")
    private BigDecimal subsidyAmount;

    @TableField("remarks")
    private String remarks;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("deleted")
    private Integer deleted;

    /** 归档时间 */
    @TableField(value = "archive_time", fill = FieldFill.INSERT)
    private LocalDateTime archiveTime;
}
