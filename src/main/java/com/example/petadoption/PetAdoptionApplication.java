package com.example.petadoption;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 宠物领养服务启动类
 */
@SpringBootApplication
@MapperScan("com.example.petadoption.mapper")
@EnableAsync
public class PetAdoptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetAdoptionApplication.class, args);
    }
}
