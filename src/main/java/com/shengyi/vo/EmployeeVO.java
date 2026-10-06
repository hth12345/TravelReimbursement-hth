package com.shengyi.vo;

import lombok.Data;

/**
 * 报销人VO
 */
@Data
public class EmployeeVO {
    /**
     * 报销人员ID
     */
    private String reimburserId;

    /**
     * 报销人员工号
     */
    private String reimburserNo;

    /**
     * 报销人员姓名
     */
    private String reimburserName;
}
