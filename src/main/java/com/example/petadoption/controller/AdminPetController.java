package com.example.petadoption.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petadoption.service.AdminPetService;
import com.example.petadoption.util.JwtUtil;
import com.example.petadoption.vo.AdminPetVO;
import com.example.petadoption.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "管理员-宠物信息管理")
@RestController
@RequestMapping("/admin/pets")
public class AdminPetController {

    @Autowired
    private AdminPetService adminPetService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("宠物信息列表")
    @GetMapping
    public Result<Page<AdminPetVO>> getPetList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer adoptionStatus,
            @RequestParam(required = false) Integer petStatus) {
        Page<AdminPetVO> page = adminPetService.getPetPage(pageNum, pageSize, keyword, categoryId, adoptionStatus, petStatus);
        return Result.success(page);
    }

    @ApiOperation("宠物详情")
    @GetMapping("/{petId}")
    public Result<AdminPetVO> getPetDetail(@PathVariable String petId) {
        AdminPetVO pet = adminPetService.getPetDetail(petId);
        return Result.success(pet);
    }

    @ApiOperation("强制下架")
    @PutMapping("/{petId}/offline")
    public Result<Void> offlinePet(@PathVariable String petId,
                                    @RequestHeader("Authorization") String authorization,
                                    HttpServletRequest request) {
        String token = authorization.replace("Bearer ", "");
        String operatorId = jwtUtil.getUserId(token);
        String operatorName = jwtUtil.getUsername(token);
        String ipAddress = getClientIp(request);
        
        adminPetService.offlinePet(operatorId, operatorName, ipAddress, petId);
        return Result.success();
    }

    @ApiOperation("删除宠物信息")
    @DeleteMapping("/{petId}")
    public Result<Void> deletePet(@PathVariable String petId,
                                   @RequestHeader("Authorization") String authorization,
                                   HttpServletRequest request) {
        String token = authorization.replace("Bearer ", "");
        String operatorId = jwtUtil.getUserId(token);
        String operatorName = jwtUtil.getUsername(token);
        String ipAddress = getClientIp(request);
        
        adminPetService.deletePet(operatorId, operatorName, ipAddress, petId);
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
