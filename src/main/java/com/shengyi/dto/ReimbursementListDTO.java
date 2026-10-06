package com.shengyi.dto;

import lombok.Data;

/**
 * 报销单列表查询数据传输对象
 * 用于封装分页查询报销单列表时的筛选条件
 */
@Data
public class ReimbursementListDTO {
    /** 报销单编号 */
    private String reimbursementNo;
    /** 报销单标题 */
    private String title;
    /** 报销事由 */
    private String reason;
    /** 公司ID */
    private String companyId;
    /** 部门ID */
    private String departmentId;
    /** 报销人ID */
    private String reimburserId;
    /** 业务类型ID */
    private String businessTypeId;
    /** 当前页码，默认第1页 */
    private int pageNo = 1;
    /** 每页条数，默认10条 */
    private int pageSize = 10;
}
