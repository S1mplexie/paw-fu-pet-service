package com.example.petadoption.controller;

import com.example.petadoption.dto.AiChatRequest;
import com.example.petadoption.dto.AiChatResponse;
import com.example.petadoption.service.AiCustomerService;
import com.example.petadoption.vo.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("AI控制器测试")
public class AiControllerTest {

    @Mock
    private AiCustomerService aiCustomerService;

    @InjectMocks
    private AiController aiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("AI聊天成功")
    void testChatSuccess() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("如何喂养幼犬？");

        AiChatResponse response = AiChatResponse.builder()
                .question("如何喂养幼犬？")
                .answer("喂养幼犬需要注意营养均衡...")
                .timestamp(System.currentTimeMillis())
                .build();

        when(aiCustomerService.chat(any(AiChatRequest.class))).thenReturn(response);

        Result<AiChatResponse> result = aiController.chat(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("如何喂养幼犬？", result.getData().getQuestion());
        assertNotNull(result.getData().getAnswer());
        verify(aiCustomerService, times(1)).chat(any(AiChatRequest.class));
    }

    @Test
    @DisplayName("AI聊天-宠物健康问题")
    void testChatHealthQuestion() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("狗狗呕吐怎么办？");

        AiChatResponse response = AiChatResponse.builder()
                .question("狗狗呕吐怎么办？")
                .answer("狗狗呕吐可能是因为...")
                .timestamp(System.currentTimeMillis())
                .build();

        when(aiCustomerService.chat(any(AiChatRequest.class))).thenReturn(response);

        Result<AiChatResponse> result = aiController.chat(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("狗狗呕吐怎么办？", result.getData().getQuestion());
        verify(aiCustomerService, times(1)).chat(any(AiChatRequest.class));
    }

    @Test
    @DisplayName("AI聊天-宠物训练问题")
    void testChatTrainingQuestion() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("如何训练猫咪使用猫砂？");

        AiChatResponse response = AiChatResponse.builder()
                .question("如何训练猫咪使用猫砂？")
                .answer("训练猫咪使用猫砂需要耐心...")
                .timestamp(System.currentTimeMillis())
                .build();

        when(aiCustomerService.chat(any(AiChatRequest.class))).thenReturn(response);

        Result<AiChatResponse> result = aiController.chat(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData().getAnswer());
        verify(aiCustomerService, times(1)).chat(any(AiChatRequest.class));
    }

    @Test
    @DisplayName("获取快捷问题成功")
    void testGetQuickQuestionsSuccess() {
        List<String> questions = Arrays.asList(
                "如何喂养幼犬？",
                "狗狗呕吐怎么办？",
                "如何训练猫咪使用猫砂？",
                "宠物驱虫多久一次？"
        );

        when(aiCustomerService.getQuickQuestions()).thenReturn(questions);

        Result<List<String>> result = aiController.getQuickQuestions();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(4, result.getData().size());
        assertTrue(result.getData().contains("如何喂养幼犬？"));
        verify(aiCustomerService, times(1)).getQuickQuestions();
    }

    @Test
    @DisplayName("AI聊天-空问题")
    void testChatEmptyQuestion() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("");

        when(aiCustomerService.chat(any(AiChatRequest.class)))
                .thenThrow(new IllegalArgumentException("问题不能为空"));

        assertThrows(IllegalArgumentException.class, () -> {
            aiController.chat(request);
        });

        verify(aiCustomerService, times(1)).chat(any(AiChatRequest.class));
    }

    @Test
    @DisplayName("AI聊天-长问题")
    void testChatLongQuestion() {
        StringBuilder longQuestion = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            longQuestion.append("测试");
        }

        AiChatRequest request = new AiChatRequest();
        request.setQuestion(longQuestion.toString());

        AiChatResponse response = AiChatResponse.builder()
                .question(longQuestion.toString())
                .answer("已收到您的问题...")
                .timestamp(System.currentTimeMillis())
                .build();

        when(aiCustomerService.chat(any(AiChatRequest.class))).thenReturn(response);

        Result<AiChatResponse> result = aiController.chat(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(aiCustomerService, times(1)).chat(any(AiChatRequest.class));
    }

    @Test
    @DisplayName("获取快捷问题-验证数量")
    void testGetQuickQuestionsCount() {
        List<String> questions = Arrays.asList(
                "问题1",
                "问题2",
                "问题3"
        );

        when(aiCustomerService.getQuickQuestions()).thenReturn(questions);

        Result<List<String>> result = aiController.getQuickQuestions();

        assertNotNull(result);
        assertEquals(3, result.getData().size());
        verify(aiCustomerService, times(1)).getQuickQuestions();
    }
}
