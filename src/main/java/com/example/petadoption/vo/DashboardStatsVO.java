package com.example.petadoption.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DashboardStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer totalUsers;
    
    private Integer totalPets;
    
    private Integer waitingForAdoption;
    
    private Integer adopted;
    
    private Integer todayNewUsers;
    
    private Integer todayNewPets;
}
