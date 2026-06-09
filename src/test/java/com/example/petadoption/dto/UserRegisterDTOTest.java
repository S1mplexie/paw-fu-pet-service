package com.example.petadoption.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("用户注册DTO测试")
public class UserRegisterDTOTest {

    @Test
    @DisplayName("设置和获取所有字段")
    void testSetAndGetAllFields() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
        dto.setPhone("13800138000");
        dto.setEmail("test@example.com");
        dto.setNickname("测试用户");

        assertEquals("testuser", dto.getUsername());
        assertEquals("password123", dto.getPassword());
        assertEquals("password123", dto.getConfirmPassword());
        assertEquals("13800138000", dto.getPhone());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("测试用户", dto.getNickname());
    }

    @Test
    @DisplayName("默认值为null")
    void testDefaultValues() {
        UserRegisterDTO dto = new UserRegisterDTO();

        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
        assertNull(dto.getConfirmPassword());
        assertNull(dto.getPhone());
        assertNull(dto.getEmail());
        assertNull(dto.getNickname());
    }

    @Test
    @DisplayName("设置可选字段为null")
    void testOptionalFieldsNull() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
        dto.setPhone(null);
        dto.setEmail(null);
        dto.setNickname(null);

        assertNull(dto.getPhone());
        assertNull(dto.getEmail());
        assertNull(dto.getNickname());
    }

    @Test
    @DisplayName("更新字段值")
    void testUpdateFields() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("olduser");
        dto.setUsername("newuser");

        assertEquals("newuser", dto.getUsername());
    }
}
