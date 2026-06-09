package com.example.petadoption.controller;

import com.example.petadoption.dto.UserLoginDTO;
import com.example.petadoption.dto.UserRegisterDTO;
import com.example.petadoption.dto.UserUpdateDTO;
import com.example.petadoption.service.UserService;
import com.example.petadoption.util.JwtUtil;
import com.example.petadoption.vo.LoginVO;
import com.example.petadoption.vo.Result;
import com.example.petadoption.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("用户控制器测试")
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("用户注册成功")
    void testRegisterSuccess() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        doNothing().when(userService).register(any(UserRegisterDTO.class));

        Result<Void> result = userController.register(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        verify(userService, times(1)).register(any(UserRegisterDTO.class));
    }

    @Test
    @DisplayName("用户注册失败-密码不一致")
    void testRegisterPasswordMismatch() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setConfirmPassword("password456");

        Result<Void> result = userController.register(dto);

        assertNotNull(result);
        assertEquals(1001, result.getCode());
        assertEquals("两次输入的密码不一致", result.getMessage());
        verify(userService, never()).register(any(UserRegisterDTO.class));
    }

    @Test
    @DisplayName("用户登录成功")
    void testLoginSuccess() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        LoginVO loginVO = new LoginVO();
        loginVO.setToken("test-token");

        when(userService.login(any(UserLoginDTO.class))).thenReturn(loginVO);

        Result<LoginVO> result = userController.login(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("test-token", result.getData().getToken());
        verify(userService, times(1)).login(any(UserLoginDTO.class));
    }

    @Test
    @DisplayName("获取用户信息成功")
    void testGetUserInfoSuccess() {
        String authorization = "Bearer test-token";
        String userId = "user123";

        UserVO userVO = new UserVO();
        userVO.setUserId(userId);
        userVO.setUsername("testuser");

        when(jwtUtil.getUserId(anyString())).thenReturn(userId);
        when(userService.getUserInfo(anyString())).thenReturn(userVO);

        Result<UserVO> result = userController.getUserInfo(authorization);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(userId, result.getData().getUserId());
        verify(jwtUtil, times(1)).getUserId("test-token");
        verify(userService, times(1)).getUserInfo(userId);
    }

    @Test
    @DisplayName("更新用户信息成功")
    void testUpdateUserInfoSuccess() {
        String authorization = "Bearer test-token";
        String userId = "user123";

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("新昵称");

        UserVO userVO = new UserVO();
        userVO.setUserId(userId);
        userVO.setNickname("新昵称");

        when(jwtUtil.getUserId(anyString())).thenReturn(userId);
        when(userService.updateUserInfo(anyString(), any(UserUpdateDTO.class))).thenReturn(userVO);

        Result<UserVO> result = userController.updateUserInfo(authorization, dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("新昵称", result.getData().getNickname());
        verify(jwtUtil, times(1)).getUserId("test-token");
        verify(userService, times(1)).updateUserInfo(eq(userId), any(UserUpdateDTO.class));
    }

    @Test
    @DisplayName("获取用户信息-无Bearer前缀")
    void testGetUserInfoWithoutBearer() {
        String authorization = "test-token";
        String userId = "user123";

        UserVO userVO = new UserVO();
        userVO.setUserId(userId);

        when(jwtUtil.getUserId(anyString())).thenReturn(userId);
        when(userService.getUserInfo(anyString())).thenReturn(userVO);

        Result<UserVO> result = userController.getUserInfo(authorization);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(jwtUtil, times(1)).getUserId("test-token");
    }
}
