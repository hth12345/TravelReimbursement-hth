package com.shengyi.validation;

import com.shengyi.dto.ReimbursementSaveDTO;
import com.shengyi.entity.Reimbursement;
import com.shengyi.exception.BusinessException;
import com.shengyi.exception.StatusCode;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 状态校验：仅草稿状态可提交
 */
@Component
@Order(1)
public class StatusValidationStrategy implements SubmitValidationStrategy {

    @Override
    public void validate(Reimbursement existing, ReimbursementSaveDTO dto, ValidationContext context) {
        if (existing.getStatus() != 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "仅草稿状态可提交");
        }
    }
}
