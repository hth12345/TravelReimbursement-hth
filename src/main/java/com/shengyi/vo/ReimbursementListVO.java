package com.shengyi.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class ReimbursementListVO {
    private Long reimbursementId;
    private String reimbursementNo;
    private Integer status;
    private String title;
    private String reason;
    private String reimburserId;
    private String reimburserName;
    private String reimburserNo;
    private String departmentId;
    private String departmentName;
    private String departmentNo;
    private String companyId;
    private String companyName;
    private String companyNo;
    private String businessTypeId;
    private String businessTypeName;
    private BigDecimal subsidyAmount;
    private LocalDateTime createTime;
}
