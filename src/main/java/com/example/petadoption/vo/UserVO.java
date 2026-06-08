package com.example.petadoption.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息VO
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String username;

    private String nickname;

    private String avatar;

    private String phone;

    private String email;

    private Integer status;
    
    // 脱敏后的手机号（用于列表展示）
    private String phoneMasked;
    
    // 脱敏后的邮箱（用于列表展示）
    private String emailMasked;
}
