package com.example.petadoption.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petadoption.entity.User;
import com.example.petadoption.vo.AdminUserVO;

public interface AdminUserService extends IService<User> {

    Page<AdminUserVO> getUserPage(Integer pageNum, Integer pageSize, String keyword, String role, Integer status);
    
    AdminUserVO getUserDetail(String userId);
    
    void disableUser(String operatorId, String operatorName, String ipAddress, String userId);
    
    void enableUser(String operatorId, String operatorName, String ipAddress, String userId);
    
    void deleteUser(String operatorId, String operatorName, String ipAddress, String userId);
}
