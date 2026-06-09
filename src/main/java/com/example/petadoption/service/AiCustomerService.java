package com.example.petadoption.service;

import com.example.petadoption.config.DeepSeekConfig;
import com.example.petadoption.dto.AiChatRequest;
import com.example.petadoption.dto.AiChatResponse;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * AI智能客服服务
 */
@Slf4j
@Service
public class AiCustomerService {
    
    @Autowired
    private DeepSeekClient deepSeekClient;
    
    @Value("${ai.prompt.context:你是Paw福宠物服务平台的AI智能客服助手，专注于宠物养护、健康、训练和领养等领域的知识咨询。}")
    private String contextPrompt;
    
    @Value("${ai.quick-questions:如何喂养幼犬？,狗狗呕吐怎么办？,如何训练猫咪使用猫砂？,宠物驱虫多久一次？}")
    private String quickQuestionsConfig;
    
    public AiChatResponse chat(AiChatRequest request) {
        String question = sanitizeInput(request.getQuestion());
        
        String fullQuestion = contextPrompt + "\n\n用户问题：" + question;
        
        String answer = deepSeekClient.chat(fullQuestion);
        
        return AiChatResponse.builder()
                .question(question)
                .answer(answer)
                .timestamp(System.currentTimeMillis())
                .build();
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
