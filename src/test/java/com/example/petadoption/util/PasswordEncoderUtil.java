package com.example.petadoption.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后密码: " + encodedPassword);
        System.out.println("验证结果: " + encoder.matches(rawPassword, encodedPassword));
        
        // 验证数据库中的密码
        String dbPassword = "$2a$10$N.zmdr9k7uOCQv37YK0Ke.Lqp3MGJfF2dX8aP9jHn6FqGQxZvBmKa";
        System.out.println("\n验证数据库密码:");
        System.out.println("验证结果: " + encoder.matches(rawPassword, dbPassword));
    }
}
