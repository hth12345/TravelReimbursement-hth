package com.shengyi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 单项补助保存 DTO
 */
@Data
public class SubsidyItemSaveDTO {
    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
    /** 实际餐补金额 */
    private BigDecimal mealAmount;
    /** 餐补勾选 */
    private Integer mealChecked;
    /** 实际交通金额 */
    private BigDecimal trafficAmount;
    /** 交通勾选 */
    private Integer trafficChecked;
    /** 实际通讯金额 */
    private BigDecimal commAmount;
    /** 通讯勾选 */
    private Integer commChecked;
}
