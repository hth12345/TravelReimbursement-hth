package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 补助金额修复任务表（本地消息表）
 */
@Data
@TableName("subsidy_fix_task")
public class SubsidyFixTask {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 待修复报销单ID */
    @TableField("reimbursement_id")
    private Long reimbursementId;

    /** 状态：0-待处理，1-已完成，2-失败 */
    @TableField("status")
    private Integer status;

    /** 重试次数 */
    @TableField("retry_count")
    private Integer retryCount;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
