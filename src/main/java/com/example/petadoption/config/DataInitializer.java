package com.example.petadoption.config;

import cn.hutool.core.util.IdUtil;
import com.example.petadoption.entity.User;
import com.example.petadoption.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 初始化测试数据
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initTestUsers();
    }

    private void initTestUsers() {
        String[] usernames = {"zhangsan", "lisi", "wangwu"};
        String[] nicknames = {"张三", "李四", "王五"};
        String[] phones = {"13800138001", "13800138002", "13800138003"};
        String[] emails = {"zhangsan@example.com", "lisi@example.com", "wangwu@example.com"};
        
        for (int i = 0; i < usernames.length; i++) {
            User existingUser = userMapper.selectByUsername(usernames[i]);
            if (existingUser == null) {
                User user = new User();
                user.setUserId(IdUtil.fastSimpleUUID());
                user.setUsername(usernames[i]);
                user.setPassword(passwordEncoder.encode("Test1234"));
                user.setPhone(phones[i]);
                user.setEmail(emails[i]);
                user.setNickname(nicknames[i]);
                user.setStatus(1);
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                user.setDeleted(0);
                
                userMapper.insert(user);
                log.info("初始化测试用户: {}, 密码: Test1234", usernames[i]);
            }
        }
    }
}
