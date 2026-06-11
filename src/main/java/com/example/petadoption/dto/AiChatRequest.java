package com.example.petadoption.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * AI聊天请求DTO
 */
@Data
public class AiChatRequest {
    
    @NotBlank(message = "问题不能为空")
    @Size(max = 1000, message = "问题长度不能超过1000字符")
    private String question;
    
    private String userId;
}
