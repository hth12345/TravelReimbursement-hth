package com.shengyi.validation;

import com.shengyi.dto.ReimbursementSaveDTO;
import com.shengyi.entity.Reimbursement;
import com.shengyi.exception.BusinessException;
import com.shengyi.exception.StatusCode;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 必填字段完整性校验
 */
@Component
@Order(3)
public class RequiredFieldValidationStrategy implements SubmitValidationStrategy {

    @Override
    public void validate(Reimbursement existing, ReimbursementSaveDTO dto, ValidationContext context) {
        Reimbursement reimbursement = existing;
        if (StringUtils.hasText(reimbursement.getTitle()) && reimbursement.getTitle().length() > 500) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "报销标题不能超过500字");
        }
        if (!StringUtils.hasText(reimbursement.getCompanyId()) || !StringUtils.hasText(reimbursement.getCompanyName())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "费用归属公司信息不完整");
        }
        if (!StringUtils.hasText(reimbursement.getDepartmentId()) || !StringUtils.hasText(reimbursement.getDepartmentName())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "报销部门信息不完整");
        }
        if (!StringUtils.hasText(reimbursement.getReimburserId()) || !StringUtils.hasText(reimbursement.getReimburserName())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "报销人信息不完整");
        }
        if (!StringUtils.hasText(reimbursement.getBusinessTypeId()) || !StringUtils.hasText(reimbursement.getBusinessTypeName())) {
            throw new BusinessException(StatusCode.REQUIRED_FIELD_EMPTY, "业务类型信息不完整");
        }
    }
}
