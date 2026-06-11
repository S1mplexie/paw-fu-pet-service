package com.example.petadoption.service;

import com.example.petadoption.config.DeepSeekConfig;
import com.example.petadoption.dto.AiChatRequest;
import com.example.petadoption.dto.AiChatResponse;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * AI智能客服服务
 */
@Slf4j
@Service
public class AiCustomerService {
    
    @Autowired
    private DeepSeekClient deepSeekClient;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Value("${ai.prompt.context:你是Paw福宠物服务平台的AI智能客服助手，专注于宠物养护、健康、训练和领养等领域的知识咨询。}")
    private String contextPrompt;
    
    @Value("${ai.quick-questions:如何喂养幼犬？,狗狗呕吐怎么办？,如何训练猫咪使用猫砂？,宠物驱虫多久一次？}")
    private String quickQuestionsConfig;
    
    @Value("${ai.daily-limit:10}")
    private Integer dailyLimit;
    
    public AiChatResponse chat(AiChatRequest request) {
        Long userId = request.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        
        checkAndIncrementUsage(userId);
        
        String question = sanitizeInput(request.getQuestion());
        
        String fullQuestion = contextPrompt + "\n\n用户问题：" + question;
        
        String answer = deepSeekClient.chat(fullQuestion);
        
        int remaining = getRemainingCount(userId);
        
        return AiChatResponse.builder()
                .question(question)
                .answer(answer)
                .timestamp(System.currentTimeMillis())
                .remainingCount(remaining)
                .build();
    }
    
    private void checkAndIncrementUsage(Long userId) {
        String key = getUsageKey(userId);
        String dateKey = getDateKey();
        
        String fullKey = key + ":" + dateKey;
        
        Object countObj = redisTemplate.opsForValue().get(fullKey);
        int currentCount = countObj != null ? ((Number) countObj).intValue() : 0;
        
        if (currentCount >= dailyLimit) {
            throw new BusinessException(ErrorCode.OPERATION_TOO_FREQUENT.getCode(), 
                "今日AI咨询次数已达上限(" + dailyLimit + "次)，请明天再试");
        }
        
        redisTemplate.opsForValue().increment(fullKey, 1);
        
        redisTemplate.expire(fullKey, 1, TimeUnit.DAYS);
        
        log.info("用户 {} 今日第 {} 次使用AI客服", userId, currentCount + 1);
    }
    
    public int getRemainingCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        
        String fullKey = getUsageKey(userId) + ":" + getDateKey();
        Object countObj = redisTemplate.opsForValue().get(fullKey);
        int currentCount = countObj != null ? ((Number) countObj).intValue() : 0;
        
        return Math.max(0, dailyLimit - currentCount);
    }
    
    private String getUsageKey(Long userId) {
        return "ai:usage:" + userId;
    }
    
    private String getDateKey() {
        return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    
    private String sanitizeInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        
        String sanitized = input.trim();
        
        sanitized = sanitized.replaceAll("<[^>]*>", "");
        
        if (sanitized.length() > 1000) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "问题长度不能超过1000字符");
        }
        
        return sanitized;
    }
    
    public List<String> getQuickQuestions() {
        return Arrays.asList(quickQuestionsConfig.split(","));
    }
}
