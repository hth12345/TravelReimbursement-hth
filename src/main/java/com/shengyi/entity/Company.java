package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
/**
 * 费用归属公司表
 *
 */
@Data
@TableName("company")
public class Company {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 费用归属公司ID
     */
    @TableField("reim_company_id")
    private String reimCompanyId;

    /**
     * 费用归属公司编号
     */
    @TableField("reim_company_no")
    private String reimCompanyNo;

    /**
     * 费用归属公司名称
     */
    @TableField("reim_company_name")
    private String reimCompanyName;
}
