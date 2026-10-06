package com.shengyi.dto;

import lombok.Data;

@Data
public class ReimbursementListDTO {
    private String reimbursementNo;
    private String title;
    private String reason;
    private String companyId;
    private String departmentId;
    private String reimburserId;
    private String businessTypeId;
    private int pageNo = 1;
    private int pageSize = 10;
}
