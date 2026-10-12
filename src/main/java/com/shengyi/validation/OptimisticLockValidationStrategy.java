package com.shengyi.validation;

import com.shengyi.dto.ReimbursementSaveDTO;
import com.shengyi.entity.Reimbursement;
import com.shengyi.exception.BusinessException;
import com.shengyi.exception.StatusCode;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 乐观锁校验：防止并发修改
 */
@Component
@Order(2)
public class OptimisticLockValidationStrategy implements SubmitValidationStrategy {

    @Override
    public void validate(Reimbursement existing, ReimbursementSaveDTO dto, ValidationContext context) {
        if (!existing.getVersion().equals(dto.getVersion())) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "数据已被他人修改，请刷新后重试");
        }
    }
}
