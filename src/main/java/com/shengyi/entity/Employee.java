package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
/**
 * 报销人表
 *
 */
@Data
@TableName("employee")
public class Employee {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 报销人员ID
     */
    @TableField("reimburser_id")
    private String reimburserId;

    /**
     * 报销人员工号
     */
    @TableField("reimburser_no")
    private String reimburserNo;

    /**
     * 报销人员姓名
     */
    @TableField("reimburser_name")
    private String reimburserName;
}
