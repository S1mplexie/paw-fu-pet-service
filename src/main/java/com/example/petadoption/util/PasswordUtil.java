package com.example.petadoption.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类
 */
public class PasswordUtil {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Test1234";
        String encoded = encoder.encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密后密码: " + encoded);
        System.out.println("验证匹配: " + encoder.matches(password, encoded));
    }
}
