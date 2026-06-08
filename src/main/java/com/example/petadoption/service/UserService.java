package com.example.petadoption.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petadoption.dto.UserLoginDTO;
import com.example.petadoption.dto.UserRegisterDTO;
import com.example.petadoption.dto.UserUpdateDTO;
import com.example.petadoption.entity.User;
import com.example.petadoption.vo.LoginVO;
import com.example.petadoption.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    void register(UserRegisterDTO dto);

    LoginVO login(UserLoginDTO dto);

    UserVO getUserInfo(String userId);

    UserVO updateUserInfo(String userId, UserUpdateDTO dto);
}
