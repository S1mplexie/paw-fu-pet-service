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
        
        if (StrUtil.isNotBlank(dto.getCoverPhotoUrl())) {
            pet.setCoverPhotoUrl(dto.getCoverPhotoUrl());
        } else if (StrUtil.isNotBlank(dto.getPhotoUrl())) {
            pet.setCoverPhotoUrl(dto.getPhotoUrl());
        }
        
        if (StrUtil.isNotBlank(dto.getPhotoUrls())) {
            pet.setPhotoUrls(dto.getPhotoUrls());
        } else if (StrUtil.isNotBlank(dto.getPhotoUrl())) {
            pet.setPhotoUrls(dto.getPhotoUrl());
        }
        
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
    public void updatePet(String userId, String petId, PetPublishDTO dto) {
        LambdaQueryWrapper<PetAdoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetAdoption::getPetId, petId);
        PetAdoption pet = this.getOne(wrapper);
        
        if (pet == null) {
            throw new BusinessException(ErrorCode.PET_NOT_FOUND);
        }
        
        if (!pet.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_PUBLISHER);
        }
        
        if (pet.getAdoptionStatus() == 2) {
            throw new BusinessException(ErrorCode.PET_ADOPTED_CANNOT_EDIT);
        }
        
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
        
        if (StrUtil.isNotBlank(dto.getCoverPhotoUrl())) {
            pet.setCoverPhotoUrl(dto.getCoverPhotoUrl());
        } else if (StrUtil.isNotBlank(dto.getPhotoUrl())) {
            pet.setCoverPhotoUrl(dto.getPhotoUrl());
        }
        
        pet.setPhotoUrls(dto.getPhotoUrls());
        
        pet.setProvince(dto.getProvince());
        pet.setCity(dto.getCity());
        pet.setDistrict(dto.getDistrict());
        pet.setAddress(dto.getAddress());
        pet.setContactName(dto.getContactName());
        pet.setContactPhone(dto.getContactPhone());
        pet.setContactWechat(dto.getContactWechat());
        
        this.updateById(pet);
        log.info("修改领养信息成功，petId: {}, userId: {}", petId, userId);
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
    public void onlinePet(String userId, String petId) {
        LambdaQueryWrapper<PetAdoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetAdoption::getPetId, petId);
        PetAdoption pet = this.getOne(wrapper);
        
        if (pet == null) {
            throw new BusinessException(ErrorCode.PET_NOT_FOUND);
        }
        
        if (!pet.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_PUBLISHER);
        }
        
        if (pet.getAdoptionStatus() != 3) {
            throw new BusinessException(ErrorCode.PET_STATUS_CANNOT_ONLINE);
        }
        
        pet.setAdoptionStatus(1);
        this.updateById(pet);
        log.info("上架领养信息成功，petId: {}, userId: {}", petId, userId);
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
        
        if (StrUtil.isNotBlank(pet.getCoverPhotoUrl())) {
            vo.setPhotoUrl(pet.getCoverPhotoUrl());
        } else if (StrUtil.isNotBlank(pet.getPhotoUrls())) {
            if (pet.getPhotoUrls().startsWith("[")) {
                try {
                    String urls = pet.getPhotoUrls().replace("[", "").replace("]", "").replace("\"", "");
                    String[] urlArray = urls.split(",");
                    if (urlArray.length > 0 && StrUtil.isNotBlank(urlArray[0].trim())) {
                        vo.setPhotoUrl(urlArray[0].trim());
                    }
                } catch (Exception e) {
                    log.warn("解析图片集JSON失败: {}", pet.getPhotoUrls());
                }
            } else {
                String[] urlArray = pet.getPhotoUrls().split(",");
                if (urlArray.length > 0) {
                    vo.setPhotoUrl(urlArray[0].trim());
                }
            }
        }
        
        return vo;
    }
}
