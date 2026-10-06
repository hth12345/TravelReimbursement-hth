package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 异动日志实体
 */
@Data
@TableName("audit_log")
public class AuditLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 操作人ID */
    @TableField("operator_id")
    private String operatorId;

    /** 操作人姓名 */
    @TableField("operator_name")
    private String operatorName;

    /** 操作时间 */
    @TableField("operation_time")
    private LocalDateTime operationTime;

    /** 操作类型（枚举 name） */
    @TableField("operation_type")
    private String operationType;

    /** 操作对象ID */
    @TableField("target_id")
    private String targetId;

    /** 操作前数据（JSON） */
    @TableField("pre_data")
    private String preData;

    /** 操作后数据（JSON） */
    @TableField("post_data")
    private String postData;

    /** 客户端IP */
    @TableField("ip_address")
    private String ipAddress;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
