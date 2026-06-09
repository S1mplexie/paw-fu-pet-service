package com.example.petadoption;

import com.example.petadoption.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("测试基类")
public abstract class BaseTest {

    @Mock
    protected JwtUtil jwtUtil;

    protected String mockToken = "Bearer mock-token";
    protected String mockUserId = "user123";
    protected String mockUsername = "testuser";

    @BeforeEach
    void baseSetUp() {
        MockitoAnnotations.initMocks(this);
    }

    protected void setupJwtMock() {
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "test-secret-key-for-unit-testing-purpose-minimum-256-bits-required");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshThreshold", 3600000L);
    }

    protected String generateMockToken(String userId, String username) {
        return "Bearer " + jwtUtil.generateToken(userId, username);
    }
}
