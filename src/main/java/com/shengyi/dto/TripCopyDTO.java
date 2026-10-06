package com.shengyi.dto;

import lombok.Data;

/**
 * 复制行程请求 DTO
 */
@Data
public class TripCopyDTO {
    /** 源行程ID */
    private String sourceTripId;
    /** 目标报销单ID */
    private String targetReimbursementId;
}
