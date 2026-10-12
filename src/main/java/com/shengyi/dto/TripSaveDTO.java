package com.shengyi.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 补录行程保存DTO
 */
@Data
public class TripSaveDTO {
    /** 行程ID */
    private String tripId;
    /** 报销单ID */
    private Long reimbursementId;
    /** 出差人ID */
    private String travelerId;
    /** 出发城市 */
    private String departureCity;
    /** 到达城市 */
    private String arrivalCity;
    /** 开始日期 */
    private LocalDate startDate;
    /** 结束日期 */
    private LocalDate endDate;
    /** 行程描述 */
    private String description;
}
