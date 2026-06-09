package com.example.petadoption.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.petadoption.entity.ViewRecord;
import com.example.petadoption.mapper.PetMapper;
import com.example.petadoption.mapper.ViewRecordMapper;
import com.example.petadoption.service.ViewStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 浏览量统计服务实现类
 */
@Slf4j
@Service
public class ViewStatServiceImpl implements ViewStatService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private ViewRecordMapper viewRecordMapper;

    @Value("${view.cache.expire:86400}")
    private long cacheExpireSeconds;

    @Value("${view.suspicious.threshold:10}")
    private int suspiciousThreshold;

    @Value("${view.suspicious.window:60}")
    private long suspiciousWindowSeconds;

    @Override
    public boolean recordView(String petId, String userId, HttpServletRequest request) {
        try {
            String userIdentifier = getUserIdentifier(userId, request);
            
            if (isSuspicious(userIdentifier)) {
                log.warn("检测到可疑刷量行为，userIdentifier: {}", userIdentifier);
                return false;
            }
            
            if (hasViewed(userIdentifier, petId)) {
                return false;
            }
            
            petMapper.incrementViewCount(petId);
            
            String viewKey = "view:record:" + userIdentifier + ":" + petId;
            redisTemplate.opsForValue().set(viewKey, System.currentTimeMillis(), cacheExpireSeconds, TimeUnit.SECONDS);
            
            String freqKey = "view:freq:" + userIdentifier;
            Long freq = redisTemplate.opsForValue().increment(freqKey);
            if (freq != null && freq == 1) {
                redisTemplate.expire(freqKey, suspiciousWindowSeconds, TimeUnit.SECONDS);
            }
            
            saveViewRecordAsync(userIdentifier, petId, userId != null ? "user" : "guest");
            
            return true;
        } catch (Exception e) {
            log.error("记录浏览失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean hasViewed(String userIdentifier, String petId) {
        try {
            String viewKey = "view:record:" + userIdentifier + ":" + petId;
            Boolean hasKey = redisTemplate.hasKey(viewKey);
            if (Boolean.TRUE.equals(hasKey)) {
                return true;
            }
            
            LambdaQueryWrapper<ViewRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ViewRecord::getUserIdentifier, userIdentifier)
                   .eq(ViewRecord::getPetId, petId)
                   .ge(ViewRecord::getViewTime, LocalDateTime.now().minusSeconds(cacheExpireSeconds));
            long count = viewRecordMapper.selectCount(wrapper);
            return count > 0;
        } catch (Exception e) {
            log.warn("检查浏览记录失败，降级查询数据库: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isSuspicious(String userIdentifier) {
        try {
            String freqKey = "view:freq:" + userIdentifier;
            Object freq = redisTemplate.opsForValue().get(freqKey);
            if (freq != null) {
                long freqCount = Long.parseLong(freq.toString());
                return freqCount > suspiciousThreshold;
            }
            return false;
        } catch (Exception e) {
            log.error("检查可疑行为失败: {}", e.getMessage());
            return false;
        }
    }

    private String getUserIdentifier(String userId, HttpServletRequest request) {
        if (StrUtil.isNotBlank(userId)) {
            return userId;
        }
        
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String identifier = cn.hutool.crypto.digest.DigestUtil.md5Hex(ip + "|" + userAgent);
        return "guest_" + identifier;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @Async
    public void saveViewRecordAsync(String userIdentifier, String petId, String userType) {
        try {
            ViewRecord record = new ViewRecord();
            record.setUserIdentifier(userIdentifier);
            record.setPetId(petId);
            record.setViewTime(LocalDateTime.now());
            record.setUserType(userType);
            viewRecordMapper.insert(record);
        } catch (Exception e) {
            log.error("保存浏览记录失败: {}", e.getMessage());
        }
    }
}
