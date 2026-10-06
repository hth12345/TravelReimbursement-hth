package com.shengyi.dto;

import lombok.Data;

/**
 * 报销单保存DTO
 */
@Data
public class ReimbursementSaveDTO {
    private String reimbursementId;
    private Integer version;
    private String title;
    private String reason;
    private String companyId;
    private String departmentId;
    private String reimburserId;
    private String businessTypeId;
    private String remarks;
}
