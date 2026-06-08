package com.example.petadoption.dto;

import lombok.Data;

/**
 * 用户更新DTO
 */
@Data
public class UserUpdateDTO {

    private String nickname;

    private String avatar;

    private String phone;

    private String email;
}
