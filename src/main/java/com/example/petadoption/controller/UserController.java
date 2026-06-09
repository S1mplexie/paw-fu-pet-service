package com.example.petadoption.controller;

import com.example.petadoption.dto.UserLoginDTO;
import com.example.petadoption.dto.UserRegisterDTO;
import com.example.petadoption.dto.UserUpdateDTO;
import com.example.petadoption.service.UserService;
import com.example.petadoption.util.JwtUtil;
import com.example.petadoption.vo.LoginVO;
import com.example.petadoption.vo.Result;
import com.example.petadoption.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return Result.error(1001, "两次输入的密码不一致");
        }
        userService.register(dto);
        return Result.success();
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        LoginVO loginVO = userService.login(dto);
        return Result.success(loginVO);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/update")
    public Result<UserVO> updateUserInfo(@RequestHeader("Authorization") String authorization,
                                         @Valid @RequestBody UserUpdateDTO dto) {
        String token = authorization.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        UserVO userVO = userService.updateUserInfo(userId, dto);
        return Result.success(userVO);
    }
}
