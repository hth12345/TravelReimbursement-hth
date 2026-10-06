package com.shengyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shengyi.dto.ReimbursementListDTO;
import com.shengyi.dto.ReimbursementSaveDTO;
import com.shengyi.entity.Reimbursement;
import com.shengyi.vo.PageResult;
import com.shengyi.vo.ReimbursementDetailVO;
import com.shengyi.vo.ReimbursementListVO;

public interface ReimbursementService extends IService<Reimbursement> {
    PageResult<ReimbursementListVO> POST_REIMBURSEMENT_LIST(ReimbursementListDTO query);

    ReimbursementDetailVO GET_REIMBURSEMENT_DETAIL(String reimbursementId);

    Long POST_REIMBURSEMENT_SAVE_DRAFT(ReimbursementSaveDTO dto);

    void POST_REIMBURSEMENT_SUBMIT(ReimbursementSaveDTO dto);

    void POST_REIMBURSEMENT_WITHDRAW(String reimbursementId);

    void POST_REIMBURSEMENT_INVALIDATE(String reimbursementId);

    void DELETE_REIMBURSEMENT(String reimbursementId);
}
