package com.example.petadoption.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DeepSeek API配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfig {
    
    private String endpoint = "https://api.deepseek.com/v1/chat/completions";
    private String apiKey;
    private Integer timeout = 15000;
    private Integer connectTimeout = 5000;
    private String model = "deepseek-chat";
    private Integer maxTokens = 2000;
}
