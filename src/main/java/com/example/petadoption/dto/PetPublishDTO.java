package com.example.petadoption.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 宠物发布DTO
 */
@Data
public class PetPublishDTO {

    @NotBlank(message = "宠物名称不能为空")
    private String petName;

    @NotNull(message = "宠物种类不能为空")
    private Integer categoryId;

    @NotNull(message = "宠物状态不能为空")
    private Integer petStatus;

    private Integer age;

    private Integer gender;

    private String color;

    private String weight;

    private String description;

    private String photoUrl;

    private String coverPhotoUrl;

    private String photoUrls;

    private String province;

    private String city;

    private String district;

    private String address;

    @NotBlank(message = "联系人不能为空")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String contactWechat;
}
