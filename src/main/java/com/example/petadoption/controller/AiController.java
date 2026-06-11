package com.example.petadoption.controller;

import com.example.petadoption.dto.AiChatRequest;
import com.example.petadoption.dto.AiChatResponse;
import com.example.petadoption.service.AiCustomerService;
import com.example.petadoption.util.JwtUtil;
import com.example.petadoption.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * AI智能客服控制器
 */
@Api(tags = "AI智能客服")
@RestController
@RequestMapping("/ai")
public class AiController {
    
    @Autowired
    private AiCustomerService aiCustomerService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @ApiOperation("智能问答")
    @PostMapping("/chat")
    public Result<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request,
                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.replace("Bearer ", "");
            String userIdStr = jwtUtil.getUserId(token);
            if (userIdStr != null) {
                userId = Long.parseLong(userIdStr);
            }
        }
        request.setUserId(userId);
        
        AiChatResponse response = aiCustomerService.chat(request);
        return Result.success(response);
    }
    
    @ApiOperation("获取快捷问题")
    @GetMapping("/quick-questions")
    public Result<List<String>> getQuickQuestions() {
        List<String> questions = aiCustomerService.getQuickQuestions();
        return Result.success(questions);
    }
    
    @ApiOperation("获取剩余咨询次数")
    @GetMapping("/remaining-count")
    public Result<Integer> getRemainingCount(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.replace("Bearer ", "");
            String userIdStr = jwtUtil.getUserId(token);
            if (userIdStr != null) {
                userId = Long.parseLong(userIdStr);
            }
        }
        
        int remaining = aiCustomerService.getRemainingCount(userId);
        return Result.success(remaining);
    }
}
