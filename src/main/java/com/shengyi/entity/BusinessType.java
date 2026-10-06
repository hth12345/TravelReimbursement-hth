package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * 业务类型表
 *
 */
@Data
@TableName("business_type")
public class BusinessType {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业务类型ID
     */
    @TableField("business_type_id")
    private String businessTypeId;

    /**
     * 业务类型编号
     */
    @TableField("business_type_no")
    private String businessTypeNo;

    /**
     * 业务类型名称
     */
    @TableField("business_type_name")
    private String businessTypeName;

    /**
     * 是否有下级节点：0-无，1-有
     */
    @TableField("there_subordinate_node")
    private String thereSubordinateNode;

    /**
     * 上级业务类型ID，none表示最上级
     */
    @TableField("superior_id")
    private String superiorId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableField("is_deleted")
    private Integer isDeleted;
}
