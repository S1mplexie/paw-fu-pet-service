package com.example.petadoption.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.petadoption.dto.AdminLoginDTO;
import com.example.petadoption.entity.User;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.exception.ErrorCode;
import com.example.petadoption.mapper.UserMapper;
import com.example.petadoption.service.AuditLogService;
import com.example.petadoption.util.JwtUtil;
import com.example.petadoption.vo.LoginVO;
import com.example.petadoption.vo.Result;
import com.example.petadoption.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@Api(tags = "管理员认证")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuditLogService auditLogService;

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody AdminLoginDTO dto, HttpServletRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ErrorCode.ADMIN_NOT_FOUND);
        }
        
        if (!"ADMIN".equals(user.getRole())) {
            throw new BusinessException(ErrorCode.NOT_ADMIN);
        }
        
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            String ipAddress = getClientIp(request);
            auditLogService.logFailure(user.getUserId(), user.getUsername(), "ADMIN",
                    "ADMIN_LOGIN", "管理员登录", "登录失败-密码错误", ipAddress, "密码错误");
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
        
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setRole(user.getRole());
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        loginVO.setUser(userVO);
        
        String ipAddress = getClientIp(request);
        auditLogService.logSuccess(user.getUserId(), user.getUsername(), "ADMIN",
                "ADMIN_LOGIN", "管理员登录", "登录成功", ipAddress);
        
        log.info("管理员登录成功，username: {}", user.getUsername());
        return Result.success(loginVO);
    }

    @ApiOperation("管理员登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization, 
                                HttpServletRequest request) {
        String token = authorization.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        String ipAddress = getClientIp(request);
        
        auditLogService.logSuccess(userId, username, "ADMIN",
                "ADMIN_LOGOUT", "管理员登出", "登出成功", ipAddress);
        
        log.info("管理员登出成功，username: {}", username);
        return Result.success();
    }

    @ApiOperation("获取管理员信息")
    @GetMapping("/info")
    public Result<UserVO> getAdminInfo(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, userId);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return Result.success(userVO);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StrUtil.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
