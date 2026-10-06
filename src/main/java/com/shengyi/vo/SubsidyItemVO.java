package com.shengyi.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 单项补助明细 VO
 */
@Data
public class SubsidyItemVO {

    /** 标准金额 */
    private BigDecimal standard;

    /** 实际金额 */
    private BigDecimal amount;

    /** 勾选状态：0-未勾选，1-已勾选 */
    private Integer checked;
}
