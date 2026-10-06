package com.shengyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 城市类型配置表
 */
@Data
@TableName("city_type_config")
public class CityTypeConfig {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("city_no")
    private String cityNo;

    @TableField("city_name")
    private String cityName;

    /** 城市类型：1-一线，2-二线，3-三线 */
    @TableField("city_type")
    private Integer cityType;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
