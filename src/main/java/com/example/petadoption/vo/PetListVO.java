package com.example.petadoption.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物列表VO
 */
@Data
public class PetListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String petId;

    private String petName;

    private Integer categoryId;

    private String categoryName;

    private Integer petStatus;

    private String petStatusName;

    private Integer age;

    private Integer gender;

    private String color;

    private BigDecimal weight;

    private String description;

    private String photoUrl;

    private String coverPhotoUrl;

    private String photoUrls;

    private String province;

    private String city;

    private String district;

    private String address;

    private String contactName;

    private String contactPhone;

    private String contactWechat;

    private Integer adoptionStatus;

    private Integer viewCount;

    private LocalDateTime createTime;

    private String publisherId;

    private String publisherName;
}
