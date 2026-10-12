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
 * 行程日期重叠校验 → 50031
 */
@Component
@Order(4)
public class TripOverlapValidationStrategy implements SubmitValidationStrategy {

    @Resource
    private ReimbursementMapper reimbursementMapper;

    @Override
    public void validate(Reimbursement existing, ReimbursementSaveDTO dto, ValidationContext context) {
        int overlapCount = reimbursementMapper.countTripDateOverlap(existing.getId());
        context.setOverlapCount(overlapCount);
        if (overlapCount > 0) {
            throw new BusinessException(StatusCode.DATE_OVERLAP, "存在行程日期重叠，请调整出行日期");
        }
    }
}
