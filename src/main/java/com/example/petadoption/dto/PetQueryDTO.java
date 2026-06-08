package com.example.petadoption.dto;

import lombok.Data;

/**
 * 宠物查询DTO
 */
@Data
public class PetQueryDTO {

    private Integer categoryId;

    private String province;

    private String city;

    private Integer adoptionStatus;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
