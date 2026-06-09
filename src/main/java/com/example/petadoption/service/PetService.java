package com.example.petadoption.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petadoption.dto.PetPublishDTO;
import com.example.petadoption.dto.PetQueryDTO;
import com.example.petadoption.entity.PetAdoption;
import com.example.petadoption.vo.PetListVO;

/**
 * 宠物领养信息服务接口
 */
public interface PetService extends IService<PetAdoption> {

    Page<PetListVO> getPetList(PetQueryDTO dto);

    PetListVO getPetDetail(String petId);

    void publishPet(String userId, PetPublishDTO dto);

    void updatePet(String userId, String petId, PetPublishDTO dto);

    Page<PetListVO> getMyPets(String userId, Integer pageNum, Integer pageSize);

    void offlinePet(String userId, String petId);

    void onlinePet(String userId, String petId);

    void deletePet(String userId, String petId);
}
