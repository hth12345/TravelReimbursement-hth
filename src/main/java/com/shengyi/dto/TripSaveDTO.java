package com.shengyi.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 补录行程保存DTO
 */
@Data
public class TripSaveDTO {
    private String tripId;
    private Long reimbursementId;
    private String travelerId;
    private String departureCity;
    private String arrivalCity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
