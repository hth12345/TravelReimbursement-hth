package com.shengyi.validation;

import com.shengyi.dto.ReimbursementSaveDTO;
import com.shengyi.entity.Reimbursement;

/**
 * 提交校验策略接口
 */
public interface SubmitValidationStrategy {
    void validate(Reimbursement existing, ReimbursementSaveDTO dto, ValidationContext context);
}
