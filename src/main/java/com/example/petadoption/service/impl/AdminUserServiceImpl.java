package com.example.petadoption.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petadoption.entity.PetAdoption;
import com.example.petadoption.entity.User;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.exception.ErrorCode;
import com.example.petadoption.mapper.PetMapper;
import com.example.petadoption.mapper.UserMapper;
import com.example.petadoption.service.AdminUserService;
import com.example.petadoption.service.AuditLogService;
import com.example.petadoption.vo.AdminUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminUserServiceImpl extends ServiceImpl<UserMapper, User> implements AdminUserService {

    @Autowired
    private PetMapper petMapper;
    
    @Autowired
    private AuditLogService auditLogService;

    @Override
    public Page<AdminUserVO> getUserPage(Integer pageNum, Integer pageSize, String keyword, String role, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getEmail, keyword));
        }
        
        if (StrUtil.isNotBlank(role)) {
            wrapper.eq(User::getRole, role);
        }
        
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> userPage = this.page(page, wrapper);
        
        Page<AdminUserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream().map(user -> {
            AdminUserVO vo = new AdminUserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(java.util.stream.Collectors.toList()));
        
        return voPage;
    }

    @Override
    public AdminUserVO getUserDetail(String userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, userId);
        User user = this.getOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        AdminUserVO vo = new AdminUserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public void disableUser(String operatorId, String operatorName, String ipAddress, String userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, userId);
        User user = this.getOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException(ErrorCode.CANNOT_DISABLE_ADMIN);
        }
        
        user.setStatus(0);
        this.updateById(user);
        
        auditLogService.logSuccess(operatorId, operatorName, "ADMIN", 
                "DISABLE_USER", "用户:" + userId, "禁用用户:" + user.getUsername(), 
                ipAddress);
        
        log.info("管理员{}禁用用户{}", operatorName, user.getUsername());
    }

    @Override
    public void enableUser(String operatorId, String operatorName, String ipAddress, String userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, userId);
        User user = this.getOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        user.setStatus(1);
        this.updateById(user);
        
        auditLogService.logSuccess(operatorId, operatorName, "ADMIN", 
                "ENABLE_USER", "用户:" + userId, "启用用户:" + user.getUsername(), 
                ipAddress);
        
        log.info("管理员{}启用用户{}", operatorName, user.getUsername());
    }

    @Override
    public void deleteUser(String operatorId, String operatorName, String ipAddress, String userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, userId);
        User user = this.getOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ADMIN);
        }
        
        LambdaQueryWrapper<PetAdoption> petWrapper = new LambdaQueryWrapper<>();
        petWrapper.eq(PetAdoption::getPublisherId, userId);
        Integer petCount = petMapper.selectCount(petWrapper);
        if (petCount > 0) {
            throw new BusinessException(ErrorCode.USER_HAS_RELATED_DATA);
        }
        
        this.removeById(user.getId());
        
        auditLogService.logSuccess(operatorId, operatorName, "ADMIN", 
                "DELETE_USER", "用户:" + userId, "删除用户:" + user.getUsername(), 
                ipAddress);
        
        log.info("管理员{}删除用户{}", operatorName, user.getUsername());
    }
}
