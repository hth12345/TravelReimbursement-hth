package com.shengyi.validation;

import lombok.Data;

/**
 * 校验上下文，用于在策略链中传递中间结果
 */
@Data
public class ValidationContext {
    /** 行程日期重叠数量 */
    private int overlapCount;
    /** 补助金额超标数量 */
    private int exceedCount;
}
