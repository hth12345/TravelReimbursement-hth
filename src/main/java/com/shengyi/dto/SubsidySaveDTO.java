package com.shengyi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 补助日历保存 DTO
 */
@Data
public class SubsidySaveDTO {
    /** 行程ID */
    private String tripId;
    /** 前端计算的补助总额（用于后端校验一致性） */
    private BigDecimal totalAmount;
    /** 补助明细列表 */
    private List<SubsidyItemSaveDTO> items;
}