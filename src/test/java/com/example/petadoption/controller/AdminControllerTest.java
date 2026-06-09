package com.example.petadoption.controller;

import com.example.petadoption.dto.AdminLoginDTO;
import com.example.petadoption.entity.User;
import com.example.petadoption.mapper.UserMapper;
import com.example.petadoption.service.AuditLogService;
import com.example.petadoption.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuditLogService auditLogService;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setUserId("admin001");
        adminUser.setUsername("admin");
        adminUser.setPassword("$2a$10$N.zmdr9k7uOCQv37YK0Ke.Lqp3MGJfF2dX8aP9jHn6FqGQxZvBmKa");
        adminUser.setRole("ADMIN");
        adminUser.setStatus(1);
    }

    @Test
    void testAdminLogin_Success() throws Exception {
        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin123");

        when(userMapper.selectOne(any())).thenReturn(adminUser);
        when(passwordEncoder.matches("admin123", adminUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("admin001", "admin", "ADMIN")).thenReturn("test-token");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("test-token"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void testAdminLogin_NotAdmin() throws Exception {
        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setUsername("user");
        dto.setPassword("password");

        User normalUser = new User();
        normalUser.setUserId("user001");
        normalUser.setUsername("user");
        normalUser.setRole("USER");

        when(userMapper.selectOne(any())).thenReturn(normalUser);

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }
}
