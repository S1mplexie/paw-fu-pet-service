package com.example.petadoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.petadoption.dto.UserLoginDTO;
import com.example.petadoption.dto.UserRegisterDTO;
import com.example.petadoption.dto.UserUpdateDTO;
import com.example.petadoption.entity.User;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.mapper.UserMapper;
import com.example.petadoption.util.JwtUtil;
import com.example.petadoption.vo.LoginVO;
import com.example.petadoption.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("用户服务测试")
public class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

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
        dto.setPhone("13800138000");
        dto.setEmail("test@example.com");
        dto.setNickname("测试用户");

        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> userService.register(dto));

        verify(userMapper, times(3)).selectCount(any(LambdaQueryWrapper.class));
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("用户注册失败-用户名已存在")
    void testRegisterUsernameExists() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1);

        assertThrows(BusinessException.class, () -> userService.register(dto));

        verify(userMapper, times(1)).selectCount(any(LambdaQueryWrapper.class));
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("用户注册失败-手机号已存在")
    void testRegisterPhoneExists() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setPhone("13800138000");

        when(userMapper.selectCount(any(LambdaQueryWrapper.class)))
                .thenReturn(0)
                .thenReturn(1);

        assertThrows(BusinessException.class, () -> userService.register(dto));

        verify(userMapper, times(2)).selectCount(any(LambdaQueryWrapper.class));
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("用户注册失败-邮箱已存在")
    void testRegisterEmailExists() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setEmail("test@example.com");

        when(userMapper.selectCount(any(LambdaQueryWrapper.class)))
                .thenReturn(0)
                .thenReturn(0)
                .thenReturn(1);

        assertThrows(BusinessException.class, () -> userService.register(dto));

        verify(userMapper, times(3)).selectCount(any(LambdaQueryWrapper.class));
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("用户登录成功")
    void testLoginSuccess() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        User user = new User();
        user.setUserId("user123");
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setNickname("测试用户");
        user.setStatus(1);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("test-token");

        LoginVO loginVO = userService.login(dto);

        assertNotNull(loginVO);
        assertEquals("test-token", loginVO.getToken());
        assertNotNull(loginVO.getUser());
        assertEquals("user123", loginVO.getUser().getUserId());
        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        verify(jwtUtil, times(1)).generateToken("user123", "testuser");
    }

    @Test
    @DisplayName("用户登录失败-用户不存在")
    void testLoginUserNotFound() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> userService.login(dto));

        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("用户登录失败-密码错误")
    void testLoginPasswordError() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        User user = new User();
        user.setUserId("user123");
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setStatus(1);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.login(dto));

        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
    }

    @Test
    @DisplayName("用户登录失败-用户被禁用")
    void testLoginUserDisabled() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        User user = new User();
        user.setUserId("user123");
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setStatus(0);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.login(dto));

        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("获取用户信息成功")
    void testGetUserInfoSuccess() {
        String userId = "user123";

        User user = new User();
        user.setUserId(userId);
        user.setUsername("testuser");
        user.setNickname("测试用户");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setStatus(1);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        UserVO userVO = userService.getUserInfo(userId);

        assertNotNull(userVO);
        assertEquals(userId, userVO.getUserId());
        assertEquals("testuser", userVO.getUsername());
        assertEquals("测试用户", userVO.getNickname());
        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("获取用户信息失败-用户不存在")
    void testGetUserInfoNotFound() {
        String userId = "user123";

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> userService.getUserInfo(userId));

        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("更新用户信息成功")
    void testUpdateUserInfoSuccess() {
        String userId = "user123";

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("新昵称");
        dto.setPhone("13900139000");
        dto.setEmail("new@example.com");
        dto.setAvatar("http://example.com/avatar.jpg");

        User user = new User();
        user.setUserId(userId);
        user.setUsername("testuser");
        user.setNickname("旧昵称");
        user.setPhone("13800138000");
        user.setEmail("old@example.com");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        UserVO userVO = userService.updateUserInfo(userId, dto);

        assertNotNull(userVO);
        assertEquals("新昵称", userVO.getNickname());
        assertEquals("13900139000", userVO.getPhone());
        assertEquals("new@example.com", userVO.getEmail());
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("更新用户信息失败-用户不存在")
    void testUpdateUserInfoNotFound() {
        String userId = "user123";

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("新昵称");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> userService.updateUserInfo(userId, dto));

        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    @DisplayName("更新用户信息失败-手机号已被使用")
    void testUpdateUserInfoPhoneUsed() {
        String userId = "user123";

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setPhone("13900139000");

        User user = new User();
        user.setUserId(userId);
        user.setPhone("13800138000");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1);

        assertThrows(BusinessException.class, () -> userService.updateUserInfo(userId, dto));

        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    @DisplayName("更新用户信息-只更新昵称")
    void testUpdateUserInfoOnlyNickname() {
        String userId = "user123";

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("新昵称");

        User user = new User();
        user.setUserId(userId);
        user.setUsername("testuser");
        user.setNickname("旧昵称");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        UserVO userVO = userService.updateUserInfo(userId, dto);

        assertNotNull(userVO);
        assertEquals("新昵称", userVO.getNickname());
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("用户注册-无手机号和邮箱")
    void testRegisterWithoutPhoneAndEmail() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setNickname("测试用户");

        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> userService.register(dto));

        verify(userMapper, times(1)).selectCount(any(LambdaQueryWrapper.class));
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("用户登录-手机号脱敏")
    void testLoginPhoneMasking() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        User user = new User();
        user.setUserId("user123");
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setPhone("13800138000");
        user.setStatus(1);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("test-token");

        LoginVO loginVO = userService.login(dto);

        assertNotNull(loginVO);
        assertNotNull(loginVO.getUser());
        assertNotNull(loginVO.getUser().getPhoneMasked());
        assertTrue(loginVO.getUser().getPhoneMasked().contains("****"));
    }
}
