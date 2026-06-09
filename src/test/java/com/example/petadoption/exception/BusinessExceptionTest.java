package com.example.petadoption.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("业务异常测试")
public class BusinessExceptionTest {

    @Test
    @DisplayName("使用消息创建异常")
    void testCreateWithMessage() {
        String message = "操作失败";

        BusinessException exception = new BusinessException(message);

        assertEquals(message, exception.getMessage());
        assertEquals(500, exception.getCode());
    }

    @Test
    @DisplayName("使用错误码和消息创建异常")
    void testCreateWithCodeAndMessage() {
        Integer code = 1001;
        String message = "用户已存在";

        BusinessException exception = new BusinessException(code, message);

        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    @DisplayName("使用错误码枚举创建异常")
    void testCreateWithErrorCode() {
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

        BusinessException exception = new BusinessException(errorCode);

        assertEquals(errorCode.getMessage(), exception.getMessage());
        assertEquals(errorCode.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("验证异常继承关系")
    void testExceptionInheritance() {
        BusinessException exception = new BusinessException("测试异常");

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("不同错误码创建异常")
    void testDifferentErrorCodes() {
        BusinessException exception1 = new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        BusinessException exception2 = new BusinessException(ErrorCode.PASSWORD_ERROR);
        BusinessException exception3 = new BusinessException(ErrorCode.PET_NOT_FOUND);

        assertEquals(ErrorCode.USER_ALREADY_EXISTS.getCode(), exception1.getCode());
        assertEquals(ErrorCode.PASSWORD_ERROR.getCode(), exception2.getCode());
        assertEquals(ErrorCode.PET_NOT_FOUND.getCode(), exception3.getCode());
    }

    @Test
    @DisplayName("获取错误码")
    void testGetCode() {
        Integer expectedCode = 1002;

        BusinessException exception = new BusinessException(expectedCode, "用户不存在");

        assertEquals(expectedCode, exception.getCode());
    }

    @Test
    @DisplayName("空消息创建异常")
    void testCreateWithEmptyMessage() {
        BusinessException exception = new BusinessException("");

        assertEquals("", exception.getMessage());
        assertEquals(500, exception.getCode());
    }

    @Test
    @DisplayName("自定义错误码测试")
    void testCustomErrorCode() {
        Integer customCode = 9999;
        String customMessage = "自定义错误";

        BusinessException exception = new BusinessException(customCode, customMessage);

        assertEquals(customCode, exception.getCode());
        assertEquals(customMessage, exception.getMessage());
    }
}
