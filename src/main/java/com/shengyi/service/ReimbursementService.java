package com.shengyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shengyi.dto.ReimbursementListDTO;
import com.shengyi.dto.ReimbursementSaveDTO;
import com.shengyi.entity.Reimbursement;
import com.shengyi.vo.PageResult;
import com.shengyi.vo.ReimbursementDetailVO;
import com.shengyi.vo.ReimbursementListVO;

public interface ReimbursementService extends IService<Reimbursement> {
    PageResult<ReimbursementListVO> getReimbursementList(ReimbursementListDTO query);

    ReimbursementDetailVO getReimbursementDetail(String reimbursementId);

    Long saveDraft(ReimbursementSaveDTO dto);

    void submitReimbursement(ReimbursementSaveDTO dto);

    void withdrawReimbursement(String reimbursementId);

    void invalidateReimbursement(String reimbursementId);

    void deleteReimbursement(String reimbursementId);
}
