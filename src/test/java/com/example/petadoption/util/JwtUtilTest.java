package com.example.petadoption.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWT工具类测试")
public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "test-secret-key-for-unit-testing-purpose-minimum-256-bits-required");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshThreshold", 3600000L);
    }

    @Test
    @DisplayName("生成Token成功")
    void testGenerateTokenSuccess() {
        String userId = "user123";
        String username = "testuser";

        String token = jwtUtil.generateToken(userId, username);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    @DisplayName("解析Token成功")
    void testParseTokenSuccess() {
        String userId = "user123";
        String username = "testuser";

        String token = jwtUtil.generateToken(userId, username);
        Claims claims = jwtUtil.parseToken(token);

        assertNotNull(claims);
        assertEquals(userId, claims.getSubject());
        assertEquals(username, claims.get("username", String.class));
    }

    @Test
    @DisplayName("获取用户ID成功")
    void testGetUserIdSuccess() {
        String userId = "user123";
        String username = "testuser";

        String token = jwtUtil.generateToken(userId, username);
        String extractedUserId = jwtUtil.getUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("获取用户名成功")
    void testGetUsernameSuccess() {
        String userId = "user123";
        String username = "testuser";

        String token = jwtUtil.generateToken(userId, username);
        String extractedUsername = jwtUtil.getUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("验证Token有效")
    void testValidateTokenValid() {
        String token = jwtUtil.generateToken("user123", "testuser");

        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("验证Token无效-格式错误")
    void testValidateTokenInvalid() {
        String invalidToken = "invalid-token";

        boolean isValid = jwtUtil.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("检查Token是否过期-未过期")
    void testIsTokenExpiredNotExpired() {
        String token = jwtUtil.generateToken("user123", "testuser");

        boolean isExpired = jwtUtil.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    @DisplayName("检查Token是否过期-已过期")
    void testIsTokenExpiredExpired() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
        
        String token = jwtUtil.generateToken("user123", "testuser");

        boolean isExpired = jwtUtil.isTokenExpired(token);

        assertTrue(isExpired);
    }

    @Test
    @DisplayName("解析Token失败-无效Token")
    void testParseTokenInvalid() {
        String invalidToken = "invalid.token.here";

        assertThrows(MalformedJwtException.class, () -> jwtUtil.parseToken(invalidToken));
    }

    @Test
    @DisplayName("生成多个Token")
    void testGenerateMultipleTokens() {
        String token1 = jwtUtil.generateToken("user1", "username1");
        String token2 = jwtUtil.generateToken("user2", "username2");

        assertNotEquals(token1, token2);
        
        assertEquals("user1", jwtUtil.getUserId(token1));
        assertEquals("user2", jwtUtil.getUserId(token2));
    }

    @Test
    @DisplayName("Token包含正确的发行时间")
    void testTokenContainsIssuedAt() {
        long beforeGeneration = System.currentTimeMillis();
        String token = jwtUtil.generateToken("user123", "testuser");
        long afterGeneration = System.currentTimeMillis();

        Claims claims = jwtUtil.parseToken(token);
        long issuedAt = claims.getIssuedAt().getTime();

        assertTrue(issuedAt >= beforeGeneration);
        assertTrue(issuedAt <= afterGeneration);
    }

    @Test
    @DisplayName("Token包含正确的过期时间")
    void testTokenContainsExpiration() {
        long beforeGeneration = System.currentTimeMillis();
        String token = jwtUtil.generateToken("user123", "testuser");
        
        Claims claims = jwtUtil.parseToken(token);
        long expiration = claims.getExpiration().getTime();
        long expectedExpiration = beforeGeneration + 86400000L;

        assertTrue(Math.abs(expiration - expectedExpiration) < 1000);
    }

    @Test
    @DisplayName("检查是否需要刷新Token")
    void testNeedRefreshToken() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", 2000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshThreshold", 3600000L);
        
        String token = jwtUtil.generateToken("user123", "testuser");

        boolean needRefresh = jwtUtil.needRefresh(token);

        assertTrue(needRefresh);
    }

    @Test
    @DisplayName("检查不需要刷新Token")
    void testNotNeedRefreshToken() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshThreshold", 1000L);
        
        String token = jwtUtil.generateToken("user123", "testuser");

        boolean needRefresh = jwtUtil.needRefresh(token);

        assertFalse(needRefresh);
    }

    @Test
    @DisplayName("验证空Token")
    void testValidateEmptyToken() {
        boolean isValid = jwtUtil.validateToken("");
        assertFalse(isValid);
    }

    @Test
    @DisplayName("验证nullToken")
    void testValidateNullToken() {
        boolean isValid = jwtUtil.validateToken(null);
        assertFalse(isValid);
    }
}
