package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
/**
 * 报销部门表
 *
 */
@Data
@TableName("depart")
public class Depart {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 报销部门ID
     */
    @TableField("reim_department_id")
    private String reimDepartmentId;

    /**
     * 报销部门编号
     */
    @TableField("reim_department_no")
    private String reimDepartmentNo;

    /**
     * 报销部门名称
     */
    @TableField("reim_department_name")
    private String reimDepartmentName;
}
