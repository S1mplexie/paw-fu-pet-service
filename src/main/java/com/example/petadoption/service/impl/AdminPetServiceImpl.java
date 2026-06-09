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
import com.example.petadoption.service.AdminPetService;
import com.example.petadoption.service.AuditLogService;
import com.example.petadoption.vo.AdminPetVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminPetServiceImpl extends ServiceImpl<PetMapper, PetAdoption> implements AdminPetService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private AuditLogService auditLogService;

    @Override
    public Page<AdminPetVO> getPetPage(Integer pageNum, Integer pageSize, String keyword,
                                        Integer categoryId, Integer adoptionStatus, Integer petStatus) {
        Page<PetAdoption> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PetAdoption> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(PetAdoption::getPetName, keyword)
                    .or().like(PetAdoption::getDescription, keyword)
                    .or().like(PetAdoption::getAddress, keyword));
        }
        
        if (categoryId != null) {
            wrapper.eq(PetAdoption::getCategoryId, categoryId);
        }
        
        if (adoptionStatus != null) {
            wrapper.eq(PetAdoption::getAdoptionStatus, adoptionStatus);
        }
        
        if (petStatus != null) {
            wrapper.eq(PetAdoption::getPetStatus, petStatus);
        }
        
        wrapper.orderByDesc(PetAdoption::getCreateTime);
        
        Page<PetAdoption> petPage = this.page(page, wrapper);
        
        java.util.List<String> publisherIds = petPage.getRecords().stream()
                .map(PetAdoption::getPublisherId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<String, String> publisherNameMap = new java.util.HashMap<>();
        if (!publisherIds.isEmpty()) {
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.in(User::getUserId, publisherIds);
            java.util.List<User> users = userMapper.selectList(userWrapper);
            publisherNameMap = users.stream()
                    .collect(Collectors.toMap(User::getUserId, User::getNickname));
        }
        
        Map<String, String> finalPublisherNameMap = publisherNameMap;
        Page<AdminPetVO> voPage = new Page<>(petPage.getCurrent(), petPage.getSize(), petPage.getTotal());
        voPage.setRecords(petPage.getRecords().stream().map(pet -> {
            AdminPetVO vo = new AdminPetVO();
            BeanUtils.copyProperties(pet, vo);
            vo.setPublisherName(finalPublisherNameMap.getOrDefault(pet.getPublisherId(), "未知用户"));
            return vo;
        }).collect(Collectors.toList()));
        
        return voPage;
    }

    @Override
    public AdminPetVO getPetDetail(String petId) {
        LambdaQueryWrapper<PetAdoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetAdoption::getPetId, petId);
        PetAdoption pet = this.getOne(wrapper);
        
        if (pet == null) {
            throw new BusinessException(ErrorCode.PET_NOT_FOUND);
        }
        
        AdminPetVO vo = new AdminPetVO();
        BeanUtils.copyProperties(pet, vo);
        
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUserId, pet.getPublisherId());
        User publisher = userMapper.selectOne(userWrapper);
        if (publisher != null) {
            vo.setPublisherName(publisher.getNickname());
        }
        
        return vo;
    }

    @Override
    public void offlinePet(String operatorId, String operatorName, String ipAddress, String petId) {
        LambdaQueryWrapper<PetAdoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetAdoption::getPetId, petId);
        PetAdoption pet = this.getOne(wrapper);
        
        if (pet == null) {
            throw new BusinessException(ErrorCode.PET_NOT_FOUND);
        }
        
        pet.setPetStatus(0);
        this.updateById(pet);
        
        auditLogService.logSuccess(operatorId, operatorName, "ADMIN", 
                "OFFLINE_PET", "宠物:" + petId, "下架宠物:" + pet.getPetName(), 
                ipAddress);
        
        log.info("管理员{}下架宠物{}", operatorName, pet.getPetName());
    }

    @Override
    public void deletePet(String operatorId, String operatorName, String ipAddress, String petId) {
        LambdaQueryWrapper<PetAdoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetAdoption::getPetId, petId);
        PetAdoption pet = this.getOne(wrapper);
        
        if (pet == null) {
            throw new BusinessException(ErrorCode.PET_NOT_FOUND);
        }
        
        this.removeById(pet.getId());
        
        auditLogService.logSuccess(operatorId, operatorName, "ADMIN", 
                "DELETE_PET", "宠物:" + petId, "删除宠物:" + pet.getPetName(), 
                ipAddress);
        
        log.info("管理员{}删除宠物{}", operatorName, pet.getPetName());
    }
}
