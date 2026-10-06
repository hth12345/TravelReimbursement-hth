package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报销单主表实体类
 */
@Data
@TableName("reimbursement")
public class Reimbursement {

    /**
     * 主键ID（自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 报销单号（全局唯一）
     */
    @TableField("reimbursement_no")
    private String reimbursementNo;

    /**
     * 报销标题
     */
    @TableField("title")
    private String title;

    /**
     * 报销事由
     */
    @TableField("reason")
    private String reason;

    /**
     * 报销单状态：0-草稿，1-审批中，2-审批通过，3-已完成，4-已作废
     */
    @TableField("status")
    private Integer status;

    /**
     * 乐观锁版本号（并发控制）
     */
    @TableField("version")
    private Integer version;

    /**
     * 报销人ID
     */
    @TableField("reimburser_id")
    private String reimburserId;

    /**
     * 报销人工号（脱敏显示）
     */
    @TableField("reimburser_no")
    private String reimburserNo;

    /**
     * 报销人姓名
     */
    @TableField("reimburser_name")
    private String reimburserName;

    /**
     * 报销部门ID
     */
    @TableField("department_id")
    private String departmentId;

    /**
     * 报销部门名称
     */
    @TableField("department_name")
    private String departmentName;

    /**
     * 报销部门编号
     */
    @TableField("department_no")
    private String departmentNo;

    /**
     * 费用归属公司ID
     */
    @TableField("company_id")
    private String companyId;

    /**
     * 费用归属公司名称
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 费用归属公司编号
     */
    @TableField("company_no")
    private String companyNo;

    /**
     * 业务类型ID
     */
    @TableField("business_type_id")
    private String businessTypeId;

    /**
     * 业务类型名称
     */
    @TableField("business_type_name")
    private String businessTypeName;

    /**
     * 补助总金额
     */
    @TableField("subsidy_amount")
    private BigDecimal subsidyAmount;

    /**
     * 备注（限1000字）
     */
    @TableField("remarks")
    private String remarks;

    /**
     * 创建时间
     * 插入时自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 插入和更新时自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0-正常，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
