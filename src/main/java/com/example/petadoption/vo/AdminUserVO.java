package com.example.petadoption.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    
    private String username;
    
    private String nickname;
    
    private String avatar;
    
    private String phone;
    
    private String email;
    
    private String role;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
