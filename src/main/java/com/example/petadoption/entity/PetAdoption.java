package com.example.petadoption.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 领养信息实体类
 */
@Data
@TableName("pet_adoption")
public class PetAdoption implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("pet_id")
    private String petId;

    @TableField("publisher_id")
    private String publisherId;

    @TableField("pet_name")
    private String petName;

    @TableField("category_id")
    private Integer categoryId;

    @TableField("pet_status")
    private Integer petStatus;

    private Integer age;

    private Integer gender;

    private String color;

    private BigDecimal weight;

    private String description;

    @TableField("photo_urls")
    private String photoUrls;

    @TableField("cover_photo_url")
    private String coverPhotoUrl;

    private String province;

    private String city;

    private String district;

    private String address;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("contact_wechat")
    private String contactWechat;

    @TableField("adoption_status")
    private Integer adoptionStatus;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("like_count")
    private Integer likeCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
