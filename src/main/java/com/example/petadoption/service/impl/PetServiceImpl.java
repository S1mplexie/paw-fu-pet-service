package com.example.petadoption.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petadoption.dto.PetPublishDTO;
import com.example.petadoption.dto.PetQueryDTO;
import com.example.petadoption.entity.PetAdoption;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.exception.ErrorCode;
import com.example.petadoption.mapper.PetMapper;
import com.example.petadoption.service.PetService;
import com.example.petadoption.vo.PetListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 宠物领养信息服务实现类
 */
@Slf4j
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, PetAdoption> implements PetService {

    @Override
    public Page<PetListVO> getPetList(PetQueryDTO dto) {
        Page<PetAdoption> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        
        LambdaQueryWrapper<PetAdoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetAdoption::getAdoptionStatus, 1);
        
        if (dto.getCategoryId() != null) {
            wrapper.eq(PetAdoption::getCategoryId, dto.getCategoryId());
        }
        if (StrUtil.isNotBlank(dto.getCity())) {
            wrapper.like(PetAdoption::getCity, dto.getCity());
        }
        
        wrapper.orderByDesc(PetAdoption::getCreateTime);
        
        Page<PetAdoption> petPage = this.page(page, wrapper);
        
        Page<PetListVO> result = new Page<>();
        result.setCurrent(petPage.getCurrent());
        result.setSize(petPage.getSize());
        result.setTotal(petPage.getTotal());
        
        List<PetListVO> voList = petPage.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
        result.setRecords(voList);
        
        return result;
    }

    @Override
    public PetListVO getPetDetail(String petId) {
        LambdaQueryWrapper<PetAdoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetAdoption::getPetId, petId);
        PetAdoption pet = this.getOne(wrapper);
        
        if (pet == null) {
            throw new BusinessException(ErrorCode.PET_NOT_FOUND);
        }
        
        pet.setViewCount(pet.getViewCount() + 1);
        this.updateById(pet);
        
        return convertToListVO(pet);
    }

    @Override
    public void publishPet(String userId, PetPublishDTO dto) {
        PetAdoption pet = new PetAdoption();
        pet.setPetId(IdUtil.fastSimpleUUID());
        pet.setPublisherId(userId);
        pet.setPetName(dto.getPetName());
        pet.setCategoryId(dto.getCategoryId());
        pet.setPetStatus(dto.getPetStatus());
        pet.setAge(dto.getAge());
        pet.setGender(dto.getGender());
        pet.setColor(dto.getColor());
        
        if (StrUtil.isNotBlank(dto.getWeight())) {
            pet.setWeight(new BigDecimal(dto.getWeight()));
        }
        
        pet.setDescription(dto.getDescription());
        pet.setPhotoUrls(dto.getPhotoUrl());
        pet.setProvince(dto.getProvince());
        pet.setCity(dto.getCity());
        pet.setDistrict(dto.getDistrict());
        pet.setAddress(dto.getAddress());
        pet.setContactName(dto.getContactName());
        pet.setContactPhone(dto.getContactPhone());
        pet.setContactWechat(dto.getContactWechat());
        pet.setAdoptionStatus(1);
        pet.setViewCount(0);
        pet.setLikeCount(0);
        pet.setDeleted(0);
        
        this.save(pet);
        log.info("发布领养信息成功，petId: {}, userId: {}", pet.getPetId(), userId);
    }

    @Override
    public Page<PetListVO> getMyPets(String userId, Integer pageNum, Integer pageSize) {
        Page<PetAdoption> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<PetAdoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetAdoption::getPublisherId, userId);
        wrapper.orderByDesc(PetAdoption::getCreateTime);
        
        Page<PetAdoption> petPage = this.page(page, wrapper);
        
        Page<PetListVO> result = new Page<>();
        result.setCurrent(petPage.getCurrent());
        result.setSize(petPage.getSize());
        result.setTotal(petPage.getTotal());
        
        List<PetListVO> voList = petPage.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
        result.setRecords(voList);
        
        return result;
    }

    @Override
    public void offlinePet(String userId, String petId) {
        LambdaQueryWrapper<PetAdoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetAdoption::getPetId, petId);
        PetAdoption pet = this.getOne(wrapper);
        
        if (pet == null) {
            throw new BusinessException(ErrorCode.PET_NOT_FOUND);
        }
        
        if (!pet.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_PUBLISHER);
        }
        
        pet.setAdoptionStatus(3);
        this.updateById(pet);
    }

    @Override
    public void deletePet(String userId, String petId) {
        LambdaQueryWrapper<PetAdoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetAdoption::getPetId, petId);
        PetAdoption pet = this.getOne(wrapper);
        
        if (pet == null) {
            throw new BusinessException(ErrorCode.PET_NOT_FOUND);
        }
        
        if (!pet.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_PUBLISHER);
        }
        
        this.removeById(pet.getId());
    }

    private PetListVO convertToListVO(PetAdoption pet) {
        PetListVO vo = new PetListVO();
        BeanUtils.copyProperties(pet, vo);
        
        String[] categoryNames = {"", "犬类", "猫类", "鸟类", "兔类", "仓鼠", "其他"};
        vo.setCategoryName(pet.getCategoryId() < categoryNames.length ? categoryNames[pet.getCategoryId()] : "其他");
        
        String[] statusNames = {"", "健康", "已绝育", "已免疫", "有特殊护理需求", "其他"};
        vo.setPetStatusName(pet.getPetStatus() < statusNames.length ? statusNames[pet.getPetStatus()] : "其他");
        
        if (StrUtil.isNotBlank(pet.getPhotoUrls())) {
            vo.setPhotoUrl(pet.getPhotoUrls().split(",")[0]);
        }
        
        return vo;
    }
}
