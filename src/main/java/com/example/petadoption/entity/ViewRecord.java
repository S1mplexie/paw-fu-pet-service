package com.example.petadoption.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 浏览记录实体类
 */
@Data
@TableName("view_record")
public class ViewRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_identifier")
    private String userIdentifier;

    @TableField("pet_id")
    private String petId;

    @TableField("view_time")
    private LocalDateTime viewTime;

    @TableField("user_type")
    private String userType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
