package com.example.petadoption.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petadoption.dto.UserLoginDTO;
import com.example.petadoption.dto.UserRegisterDTO;
import com.example.petadoption.dto.UserUpdateDTO;
import com.example.petadoption.entity.User;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.exception.ErrorCode;
import com.example.petadoption.mapper.UserMapper;
import com.example.petadoption.service.UserService;
import com.example.petadoption.util.JwtUtil;
import com.example.petadoption.vo.LoginVO;
import com.example.petadoption.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void register(UserRegisterDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        if (StrUtil.isNotBlank(dto.getPhone())) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, dto.getPhone());
            if (this.count(wrapper) > 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "手机号已被注册");
            }
        }

        if (StrUtil.isNotBlank(dto.getEmail())) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, dto.getEmail());
            if (this.count(wrapper) > 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "邮箱已被注册");
            }
        }

        User user = new User();
        user.setUserId(IdUtil.fastSimpleUUID());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setNickname(StrUtil.isNotBlank(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setStatus(1);
        user.setDeleted(0);

        this.save(user);
        log.info("用户注册成功，userId: {}, username: {}", user.getUserId(), user.getUsername());
    }

    @Override
    public LoginVO login(UserLoginDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 登录返回时显示脱敏数据
        userVO.setPhoneMasked(this.maskPhone(user.getPhone()));
        userVO.setEmailMasked(this.maskEmail(user.getEmail()));
        loginVO.setUser(userVO);

        log.info("用户登录成功，userId: {}, username: {}", user.getUserId(), user.getUsername());
        return loginVO;
    }

    @Override
    public UserVO getUserInfo(String userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, userId);
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 个人中心显示原始数据，不脱敏
        return userVO;
    }

    @Override
    public UserVO updateUserInfo(String userId, UserUpdateDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, userId);
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (StrUtil.isNotBlank(dto.getPhone())) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, dto.getPhone());
            wrapper.ne(User::getUserId, userId);
            if (this.count(wrapper) > 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "手机号已被使用");
            }
        }

        if (StrUtil.isNotBlank(dto.getEmail())) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, dto.getEmail());
            wrapper.ne(User::getUserId, userId);
            if (this.count(wrapper) > 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "邮箱已被使用");
            }
        }

        if (StrUtil.isNotBlank(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (StrUtil.isNotBlank(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }
        if (StrUtil.isNotBlank(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (StrUtil.isNotBlank(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }

        this.updateById(user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 修改后返回原始数据，不脱敏
        return userVO;
    }

    private String maskPhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return null;
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    private String maskEmail(String email) {
        if (StrUtil.isBlank(email)) {
            return null;
        }
        return email.replaceAll("(\\w{2})\\w+(@\\w+\\.\\w+)", "$1***$2");
    }
}
