package com.example.petadoption.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petadoption.entity.PetAdoption;
import com.example.petadoption.vo.AdminPetVO;

public interface AdminPetService extends IService<PetAdoption> {

    Page<AdminPetVO> getPetPage(Integer pageNum, Integer pageSize, String keyword, 
                                 Integer categoryId, Integer adoptionStatus, Integer petStatus);
    
    AdminPetVO getPetDetail(String petId);
    
    void offlinePet(String operatorId, String operatorName, String ipAddress, String petId);
    
    void deletePet(String operatorId, String operatorName, String ipAddress, String petId);
}
