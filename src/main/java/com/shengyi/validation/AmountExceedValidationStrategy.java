package com.shengyi.validation;

import com.shengyi.dto.ReimbursementSaveDTO;
import com.shengyi.entity.Reimbursement;
import com.shengyi.exception.BusinessException;
import com.shengyi.exception.StatusCode;
import com.shengyi.mapper.ReimbursementMapper;
import jakarta.annotation.Resource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 补助金额超标校验 → 50021
 */
@Component
@Order(5)
public class AmountExceedValidationStrategy implements SubmitValidationStrategy {

    @Resource
    private ReimbursementMapper reimbursementMapper;

    @Override
    public void validate(Reimbursement existing, ReimbursementSaveDTO dto, ValidationContext context) {
        int exceedCount = reimbursementMapper.countSubsidyAmountExceed(existing.getId());
        context.setExceedCount(exceedCount);
        if (exceedCount > 0) {
            throw new BusinessException(StatusCode.AMOUNT_EXCEED_STANDARD, "补助金额超过标准，请调整补助金额");
        }
    }
}
