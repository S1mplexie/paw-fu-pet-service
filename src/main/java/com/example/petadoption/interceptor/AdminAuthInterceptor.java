package com.example.petadoption.interceptor;

import cn.hutool.core.util.StrUtil;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.exception.ErrorCode;
import com.example.petadoption.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        
        if (StrUtil.isBlank(token) || !token.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_MISSING);
        }
        
        token = token.substring(7);
        
        try {
            if (!jwtUtil.validateToken(token)) {
                throw new BusinessException(ErrorCode.TOKEN_INVALID);
            }
            
            String role = jwtUtil.getRole(token);
            if (!"ADMIN".equals(role)) {
                throw new BusinessException(ErrorCode.NOT_ADMIN);
            }
            
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员权限验证失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
    }
}
