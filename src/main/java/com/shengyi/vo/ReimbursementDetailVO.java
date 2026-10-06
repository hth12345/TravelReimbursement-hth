package com.shengyi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 报销单详情VO
 * 对应接口文档 2.2 获取报销单详情 出参
 */
@Data
public class ReimbursementDetailVO {
    /**
     * 报销单ID
     */
    private String reimbursementId;

    /**
     * 报销单号
     */
    private String reimbursementNo;

    /**
     * 报销标题（限500字）
     */
    private String title;

    /**
     * 出差事由（限500字）
     */
    private String reason;

    /**
     * 单据状态
     * 0-草稿，1-审批中
     */
    private Integer status;

    /**
     * 乐观锁版本号（提交时使用）
     */
    private Integer version;

    /**
     * 报销人ID
     */
    private String reimburserId;

    /**
     * 报销人姓名
     */
    private String reimburserName;

    /**
     * 报销人工号
     */
    private String reimburserNo;

    /**
     * 报销部门ID
     */
    private String departmentId;

    /**
     * 报销部门名称
     */
    private String departmentName;

    /**
     * 报销部门编号
     */
    private String departmentNo;

    /**
     * 费用归属公司ID
     */
    private String companyId;

    /**
     * 费用归属公司名称
     */
    private String companyName;

    /**
     * 费用归属公司编号
     */
    private String companyNo;

    /**
     * 业务类型ID
     */
    private String businessTypeId;

    /**
     * 业务类型名称
     */
    private String businessTypeName;

    /**
     * 补助总金额
     */
    private BigDecimal subsidyAmount;

    /**
     * 备注（限1000字）
     */
    private String remarks;

    /**
     * 创建时间
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 补录行程列表
     */
    private List<TripVO> tripList;

    /**
     * 补助信息汇总列表
     */
    private List<SubsidySummaryVO> subsidySummaryList;

    /**
     * 补录行程VO
     * 对应接口文档 2.2 中 TripVO
     */
}
