package com.example.petadoption.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petadoption.service.AdminUserService;
import com.example.petadoption.util.JwtUtil;
import com.example.petadoption.vo.AdminUserVO;
import com.example.petadoption.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "管理员-用户管理")
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("用户列表")
    @GetMapping
    public Result<Page<AdminUserVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {
        Page<AdminUserVO> page = adminUserService.getUserPage(pageNum, pageSize, keyword, role, status);
        return Result.success(page);
    }

    @ApiOperation("用户详情")
    @GetMapping("/{userId}")
    public Result<AdminUserVO> getUserDetail(@PathVariable String userId) {
        AdminUserVO user = adminUserService.getUserDetail(userId);
        return Result.success(user);
    }

    @ApiOperation("禁用用户")
    @PutMapping("/{userId}/disable")
    public Result<Void> disableUser(@PathVariable String userId,
                                     @RequestHeader("Authorization") String authorization,
                                     HttpServletRequest request) {
        String token = authorization.replace("Bearer ", "");
        String operatorId = jwtUtil.getUserId(token);
        String operatorName = jwtUtil.getUsername(token);
        String ipAddress = getClientIp(request);
        
        adminUserService.disableUser(operatorId, operatorName, ipAddress, userId);
        return Result.success();
    }

    @ApiOperation("启用用户")
    @PutMapping("/{userId}/enable")
    public Result<Void> enableUser(@PathVariable String userId,
                                    @RequestHeader("Authorization") String authorization,
                                    HttpServletRequest request) {
        String token = authorization.replace("Bearer ", "");
        String operatorId = jwtUtil.getUserId(token);
        String operatorName = jwtUtil.getUsername(token);
        String ipAddress = getClientIp(request);
        
        adminUserService.enableUser(operatorId, operatorName, ipAddress, userId);
        return Result.success();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable String userId,
                                    @RequestHeader("Authorization") String authorization,
                                    HttpServletRequest request) {
        String token = authorization.replace("Bearer ", "");
        String operatorId = jwtUtil.getUserId(token);
        String operatorName = jwtUtil.getUsername(token);
        String ipAddress = getClientIp(request);
        
        adminUserService.deleteUser(operatorId, operatorName, ipAddress, userId);
        return Result.success();
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
