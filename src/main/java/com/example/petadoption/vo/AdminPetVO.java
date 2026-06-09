package com.example.petadoption.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminPetVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String petId;
    
    private String publisherId;
    
    private String publisherName;
    
    private String petName;
    
    private Integer categoryId;
    
    private Integer petStatus;
    
    private Integer age;
    
    private Integer gender;
    
    private String color;
    
    private BigDecimal weight;
    
    private String description;
    
    private String photoUrls;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private String address;
    
    private Integer adoptionStatus;
    
    private Integer viewCount;
    
    private Integer likeCount;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
