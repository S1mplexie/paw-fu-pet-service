package com.example.petadoption.service;

import com.example.petadoption.config.DeepSeekConfig;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * DeepSeek API客户端
 */
@Slf4j
@Service
public class DeepSeekClient {
    
    @Autowired
    private DeepSeekConfig config;
    
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    
    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    public String chat(String question) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", question);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModel());
            requestBody.put("messages", Collections.singletonList(message));
            requestBody.put("max_tokens", config.getMaxTokens());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());
            
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                config.getEndpoint(),
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            return parseResponse(response.getBody());
        } catch (Exception e) {
            log.error("DeepSeek API调用失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "AI服务暂时不可用，请稍后再试");
        }
    }
    
    private String parseResponse(Map response) {
        try {
            List<Map> choices = (List<Map>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map message = (Map) choices.get(0).get("message");
                String content = (String) message.get("content");
                return cleanMarkdownFormat(content);
            }
            return "抱歉，我暂时无法回答这个问题。";
        } catch (Exception e) {
            log.error("解析DeepSeek响应失败: {}", e.getMessage());
            return "抱歉，回答解析失败。";
        }
    }
    
    private String cleanMarkdownFormat(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String cleaned = text;
        
        cleaned = cleaned.replaceAll("\\*\\*\\*([^*]+)\\*\\*\\*", "$1");
        
        cleaned = cleaned.replaceAll("\\*\\*([^*]+)\\*\\*", "$1");
        
        cleaned = cleaned.replaceAll("\\*([^*]+)\\*", "$1");
        
        cleaned = cleaned.replaceAll("^[ \\t]*\\*[ \\t]+", "• ");
        cleaned = cleaned.replaceAll("\\n[ \\t]*\\*[ \\t]+", "\n• ");
        
        cleaned = cleaned.replaceAll("#{1,6}[ \\t]+", "");
        
        cleaned = cleaned.replaceAll("`([^`]+)`", "$1");
        
        cleaned = cleaned.replaceAll("```[\\w]*\\n?", "");
        cleaned = cleaned.replaceAll("```", "");
        
        return cleaned;
    }
}
