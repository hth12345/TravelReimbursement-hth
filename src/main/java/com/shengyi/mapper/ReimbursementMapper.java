package com.shengyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shengyi.dto.ReimbursementListDTO;
import com.shengyi.entity.Reimbursement;
import com.shengyi.vo.ReimbursementDetailVO;
import com.shengyi.vo.ReimbursementListVO;
import com.shengyi.vo.SubsidySummaryVO;
import com.shengyi.vo.TripVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ReimbursementMapper extends BaseMapper<Reimbursement> {
    Page<ReimbursementListVO> POST_REIMBURSEMENT_LIST(Page<ReimbursementListVO> page, @Param("query") ReimbursementListDTO query);

    ReimbursementDetailVO GET_REIMBURSEMENT_DETAIL(@Param("reimbursementId") Long reimbursementId);

    List<TripVO> GET_TRIPS_BY_REIMBURSEMENT_ID(@Param("reimbursementId") Long reimbursementId);

    List<SubsidySummaryVO> GET_SUBSIDY_SUMMARY_BY_REIMBURSEMENT_ID(@Param("reimbursementId") Long reimbursementId);

    int UPDATE_DRAFT_WITH_VERSION(Reimbursement reimbursement);

    /**
     * 提交草稿：状态 0→1（带乐观锁版本号 + 状态校验）
     */
    @Update("UPDATE reimbursement SET status = 1, version = version + 1, update_time = NOW() " +
            "WHERE id = #{id} AND version = #{version} AND status = 0")
    int SUBMIT_DRAFT(@Param("id") Long id, @Param("version") Integer version);

    /**
     * 撤回报销单：状态 1（审批中）→ 0（草稿），带状态校验
     */
    @Update("UPDATE reimbursement SET status = 0, version = version + 1, update_time = NOW() " +
            "WHERE id = #{id} AND status = 1")
    int WITHDRAW(@Param("id") Long id);

    /**
     * 作废报销单：状态 0/1/2 → 4（已作废），仅非终态可作废
     */
    @Update("UPDATE reimbursement SET status = 4, version = version + 1, update_time = NOW() " +
            "WHERE id = #{id} AND status IN (0, 1, 2)")
    int INVALIDATE(@Param("id") Long id);

    /**
     * 逻辑删除报销单：草稿（0）和已作废（4）状态可删除，设 deleted=1
     */
    @Update("UPDATE reimbursement SET deleted = 1, update_time = NOW() WHERE id = #{id} AND status IN (0, 4) AND deleted = 0")
    int DELETE_BY_ID(@Param("id") Long id);

    int COUNT_TRIP_DATE_OVERLAP(@Param("reimbursementId") Long reimbursementId);

    long COUNT_TRIPS_BY_REIMBURSEMENT_ID(@Param("reimbursementId") Long reimbursementId);

    long COUNT_SUBSIDY_BY_REIMBURSEMENT_ID(@Param("reimbursementId") Long reimbursementId);

    /**
     * 校验补助金额是否超过标准
     * @return 超标记录数
     */
    @Select("SELECT COUNT(*) FROM subsidy_calendar " +
            "WHERE reimbursement_id = #{reimbursementId} " +
            "AND (meal_amount > meal_standard OR traffic_amount > traffic_standard OR comm_amount > comm_standard)")
    int COUNT_SUBSIDY_AMOUNT_EXCEED(@Param("reimbursementId") Long reimbursementId);
}
