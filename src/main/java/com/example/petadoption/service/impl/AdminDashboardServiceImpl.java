package com.example.petadoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.petadoption.entity.PetAdoption;
import com.example.petadoption.entity.User;
import com.example.petadoption.mapper.PetMapper;
import com.example.petadoption.mapper.UserMapper;
import com.example.petadoption.service.AdminDashboardService;
import com.example.petadoption.vo.DashboardStatsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PetMapper petMapper;

    @Override
    public DashboardStatsVO getStats() {
        DashboardStatsVO vo = new DashboardStatsVO();
        
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        Integer totalUsers = userMapper.selectCount(userWrapper);
        vo.setTotalUsers(totalUsers);
        
        LambdaQueryWrapper<PetAdoption> petWrapper = new LambdaQueryWrapper<>();
        Integer totalPets = petMapper.selectCount(petWrapper);
        vo.setTotalPets(totalPets);
        
        petWrapper = new LambdaQueryWrapper<>();
        petWrapper.eq(PetAdoption::getAdoptionStatus, 0);
        Integer waitingForAdoption = petMapper.selectCount(petWrapper);
        vo.setWaitingForAdoption(waitingForAdoption);
        
        petWrapper = new LambdaQueryWrapper<>();
        petWrapper.eq(PetAdoption::getAdoptionStatus, 1);
        Integer adopted = petMapper.selectCount(petWrapper);
        vo.setAdopted(adopted);
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        userWrapper = new LambdaQueryWrapper<>();
        userWrapper.ge(User::getCreateTime, todayStart);
        Integer todayNewUsers = userMapper.selectCount(userWrapper);
        vo.setTodayNewUsers(todayNewUsers);
        
        petWrapper = new LambdaQueryWrapper<>();
        petWrapper.ge(PetAdoption::getCreateTime, todayStart);
        Integer todayNewPets = petMapper.selectCount(petWrapper);
        vo.setTodayNewPets(todayNewPets);
        
        return vo;
    }
}
