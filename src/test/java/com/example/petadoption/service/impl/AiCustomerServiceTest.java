package com.example.petadoption.service.impl;

import com.example.petadoption.config.DeepSeekConfig;
import com.example.petadoption.dto.AiChatRequest;
import com.example.petadoption.dto.AiChatResponse;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.service.AiCustomerService;
import com.example.petadoption.service.DeepSeekClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("AI客服服务测试")
public class AiCustomerServiceTest {

    @Mock
    private DeepSeekClient deepSeekClient;

    @InjectMocks
    private AiCustomerService aiCustomerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(aiCustomerService, "contextPrompt", "你是Paw福宠物服务平台的AI智能客服助手");
        ReflectionTestUtils.setField(aiCustomerService, "quickQuestionsConfig", "如何喂养幼犬？,狗狗呕吐怎么办？,如何训练猫咪使用猫砂？,宠物驱虫多久一次？");
    }

    @Test
    @DisplayName("AI聊天成功")
    void testChatSuccess() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("如何喂养幼犬？");

        when(deepSeekClient.chat(anyString())).thenReturn("喂养幼犬需要注意营养均衡，建议选择幼犬专用狗粮...");

        AiChatResponse response = aiCustomerService.chat(request);

        assertNotNull(response);
        assertEquals("如何喂养幼犬？", response.getQuestion());
        assertNotNull(response.getAnswer());
        assertNotNull(response.getTimestamp());
        verify(deepSeekClient, times(1)).chat(anyString());
    }

    @Test
    @DisplayName("AI聊天-健康问题")
    void testChatHealthQuestion() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("狗狗呕吐怎么办？");

        when(deepSeekClient.chat(anyString())).thenReturn("狗狗呕吐可能是因为消化不良...");

        AiChatResponse response = aiCustomerService.chat(request);

        assertNotNull(response);
        assertEquals("狗狗呕吐怎么办？", response.getQuestion());
        assertTrue(response.getAnswer().contains("消化不良"));
        verify(deepSeekClient, times(1)).chat(anyString());
    }

    @Test
    @DisplayName("AI聊天-训练问题")
    void testChatTrainingQuestion() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("如何训练猫咪使用猫砂？");

        when(deepSeekClient.chat(anyString())).thenReturn("训练猫咪使用猫砂需要耐心...");

        AiChatResponse response = aiCustomerService.chat(request);

        assertNotNull(response);
        assertEquals("如何训练猫咪使用猫砂？", response.getQuestion());
        verify(deepSeekClient, times(1)).chat(anyString());
    }

    @Test
    @DisplayName("AI聊天失败-空问题")
    void testChatEmptyQuestion() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("");

        assertThrows(BusinessException.class, () -> aiCustomerService.chat(request));

        verify(deepSeekClient, never()).chat(anyString());
    }

    @Test
    @DisplayName("AI聊天失败-null问题")
    void testChatNullQuestion() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion(null);

        assertThrows(BusinessException.class, () -> aiCustomerService.chat(request));

        verify(deepSeekClient, never()).chat(anyString());
    }

    @Test
    @DisplayName("AI聊天失败-空格问题")
    void testChatBlankQuestion() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("   ");

        assertThrows(BusinessException.class, () -> aiCustomerService.chat(request));

        verify(deepSeekClient, never()).chat(anyString());
    }

    @Test
    @DisplayName("AI聊天-过滤HTML标签")
    void testChatHtmlTagFilter() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("<script>alert('xss')</script>如何喂养幼犬？");

        when(deepSeekClient.chat(anyString())).thenReturn("已收到您的问题...");

        AiChatResponse response = aiCustomerService.chat(request);

        assertNotNull(response);
        assertFalse(response.getQuestion().contains("<script>"));
        verify(deepSeekClient, times(1)).chat(anyString());
    }

    @Test
    @DisplayName("AI聊天失败-超长问题")
    void testChatTooLongQuestion() {
        StringBuilder longQuestion = new StringBuilder();
        for (int i = 0; i < 150; i++) {
            longQuestion.append("测试问题内容");
        }

        AiChatRequest request = new AiChatRequest();
        request.setQuestion(longQuestion.toString());

        assertThrows(BusinessException.class, () -> aiCustomerService.chat(request));

        verify(deepSeekClient, never()).chat(anyString());
    }

    @Test
    @DisplayName("获取快捷问题成功")
    void testGetQuickQuestionsSuccess() {
        List<String> questions = aiCustomerService.getQuickQuestions();

        assertNotNull(questions);
        assertEquals(4, questions.size());
        assertTrue(questions.contains("如何喂养幼犬？"));
        assertTrue(questions.contains("狗狗呕吐怎么办？"));
        assertTrue(questions.contains("如何训练猫咪使用猫砂？"));
        assertTrue(questions.contains("宠物驱虫多久一次？"));
    }

    @Test
    @DisplayName("AI聊天-问题前后空格处理")
    void testChatQuestionTrim() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("  如何喂养幼犬？  ");

        when(deepSeekClient.chat(anyString())).thenReturn("已收到您的问题...");

        AiChatResponse response = aiCustomerService.chat(request);

        assertNotNull(response);
        assertEquals("如何喂养幼犬？", response.getQuestion());
        verify(deepSeekClient, times(1)).chat(anyString());
    }

    @Test
    @DisplayName("AI聊天-验证上下文提示")
    void testChatContextPrompt() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("测试问题");

        when(deepSeekClient.chat(anyString())).thenReturn("测试回答");

        aiCustomerService.chat(request);

        verify(deepSeekClient, times(1)).chat(argThat(msg -> 
            msg.contains("你是Paw福宠物服务平台的AI智能客服助手") && 
            msg.contains("测试问题")
        ));
    }

    @Test
    @DisplayName("AI聊天-包含特殊字符")
    void testChatWithSpecialCharacters() {
        AiChatRequest request = new AiChatRequest();
        request.setQuestion("狗狗的体重应该在10-20kg之间吗？");

        when(deepSeekClient.chat(anyString())).thenReturn("是的，这个体重范围比较合理...");

        AiChatResponse response = aiCustomerService.chat(request);

        assertNotNull(response);
        assertTrue(response.getQuestion().contains("10-20kg"));
        verify(deepSeekClient, times(1)).chat(anyString());
    }
}
