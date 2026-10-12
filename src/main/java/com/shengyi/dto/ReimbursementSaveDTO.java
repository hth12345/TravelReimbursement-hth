package com.shengyi.dto;

import lombok.Data;

/**
 * 报销单保存DTO
 */
@Data
public class ReimbursementSaveDTO {
    /** 报销单ID */
    private String reimbursementId;
    /** 版本号（乐观锁） */
    private Integer version;
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
    /** 备注 */
    private String remarks;
}
