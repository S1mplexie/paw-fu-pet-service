package com.example.petadoption.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petadoption.dto.PetPublishDTO;
import com.example.petadoption.dto.PetQueryDTO;
import com.example.petadoption.service.PetService;
import com.example.petadoption.service.ViewStatService;
import com.example.petadoption.util.JwtUtil;
import com.example.petadoption.vo.PetListVO;
import com.example.petadoption.vo.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("宠物控制器测试")
public class PetControllerTest {

    @Mock
    private PetService petService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ViewStatService viewStatService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PetController petController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("查询宠物列表成功")
    void testGetPetListSuccess() {
        PetQueryDTO dto = new PetQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        Page<PetListVO> page = new Page<>();
        page.setTotal(0);
        page.setRecords(new ArrayList<>());

        when(petService.getPetList(any(PetQueryDTO.class))).thenReturn(page);

        Result<Page<PetListVO>> result = petController.getPetList(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(petService, times(1)).getPetList(any(PetQueryDTO.class));
    }

    @Test
    @DisplayName("查询宠物详情成功-有认证")
    void testGetPetDetailWithAuth() {
        String petId = "pet123";
        String authorization = "Bearer test-token";
        String userId = "user123";

        PetListVO vo = new PetListVO();
        vo.setPetId(petId);
        vo.setPetName("小白");

        when(jwtUtil.getUserId(anyString())).thenReturn(userId);
        when(petService.getPetDetail(anyString())).thenReturn(vo);
        doNothing().when(viewStatService).recordView(anyString(), anyString(), any(HttpServletRequest.class));

        Result<PetListVO> result = petController.getPetDetail(petId, request, authorization);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(petId, result.getData().getPetId());
        verify(viewStatService, times(1)).recordView(eq(petId), eq(userId), any(HttpServletRequest.class));
        verify(petService, times(1)).getPetDetail(petId);
    }

    @Test
    @DisplayName("查询宠物详情成功-无认证")
    void testGetPetDetailWithoutAuth() {
        String petId = "pet123";

        PetListVO vo = new PetListVO();
        vo.setPetId(petId);
        vo.setPetName("小白");

        when(petService.getPetDetail(anyString())).thenReturn(vo);
        doNothing().when(viewStatService).recordView(anyString(), any(), any(HttpServletRequest.class));

        Result<PetListVO> result = petController.getPetDetail(petId, request, null);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(viewStatService, times(1)).recordView(eq(petId), isNull(), any(HttpServletRequest.class));
        verify(petService, times(1)).getPetDetail(petId);
    }

    @Test
    @DisplayName("发布宠物信息成功")
    void testPublishPetSuccess() {
        String authorization = "Bearer test-token";
        String userId = "user123";

        PetPublishDTO dto = new PetPublishDTO();
        dto.setPetName("小白");
        dto.setCategoryId(1);
        dto.setPetStatus(1);
        dto.setContactName("张三");
        dto.setContactPhone("13800138000");

        when(jwtUtil.getUserId(anyString())).thenReturn(userId);
        doNothing().when(petService).publishPet(anyString(), any(PetPublishDTO.class));

        Result<Void> result = petController.publishPet(authorization, dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(jwtUtil, times(1)).getUserId("test-token");
        verify(petService, times(1)).publishPet(eq(userId), any(PetPublishDTO.class));
    }

    @Test
    @DisplayName("更新宠物信息成功")
    void testUpdatePetSuccess() {
        String authorization = "Bearer test-token";
        String userId = "user123";
        String petId = "pet123";

        PetPublishDTO dto = new PetPublishDTO();
        dto.setPetName("小白");
        dto.setCategoryId(1);
        dto.setPetStatus(1);
        dto.setContactName("张三");
        dto.setContactPhone("13800138000");

        when(jwtUtil.getUserId(anyString())).thenReturn(userId);
        doNothing().when(petService).updatePet(anyString(), anyString(), any(PetPublishDTO.class));

        Result<Void> result = petController.updatePet(authorization, petId, dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(jwtUtil, times(1)).getUserId("test-token");
        verify(petService, times(1)).updatePet(eq(userId), eq(petId), any(PetPublishDTO.class));
    }

    @Test
    @DisplayName("查询我的发布成功")
    void testGetMyPetsSuccess() {
        String authorization = "Bearer test-token";
        String userId = "user123";

        Page<PetListVO> page = new Page<>();
        page.setTotal(0);
        page.setRecords(new ArrayList<>());

        when(jwtUtil.getUserId(anyString())).thenReturn(userId);
        when(petService.getMyPets(anyString(), anyInt(), anyInt())).thenReturn(page);

        Result<Page<PetListVO>> result = petController.getMyPets(authorization, 1, 10);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(jwtUtil, times(1)).getUserId("test-token");
        verify(petService, times(1)).getMyPets(userId, 1, 10);
    }

    @Test
    @DisplayName("下架宠物信息成功")
    void testOfflinePetSuccess() {
        String authorization = "Bearer test-token";
        String userId = "user123";
        String petId = "pet123";

        when(jwtUtil.getUserId(anyString())).thenReturn(userId);
        doNothing().when(petService).offlinePet(anyString(), anyString());

        Result<Void> result = petController.offlinePet(authorization, petId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(jwtUtil, times(1)).getUserId("test-token");
        verify(petService, times(1)).offlinePet(userId, petId);
    }

    @Test
    @DisplayName("上架宠物信息成功")
    void testOnlinePetSuccess() {
        String authorization = "Bearer test-token";
        String userId = "user123";
        String petId = "pet123";

        when(jwtUtil.getUserId(anyString())).thenReturn(userId);
        doNothing().when(petService).onlinePet(anyString(), anyString());

        Result<Void> result = petController.onlinePet(authorization, petId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(jwtUtil, times(1)).getUserId("test-token");
        verify(petService, times(1)).onlinePet(userId, petId);
    }

    @Test
    @DisplayName("删除宠物信息成功")
    void testDeletePetSuccess() {
        String authorization = "Bearer test-token";
        String userId = "user123";
        String petId = "pet123";

        when(jwtUtil.getUserId(anyString())).thenReturn(userId);
        doNothing().when(petService).deletePet(anyString(), anyString());

        Result<Void> result = petController.deletePet(authorization, petId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(jwtUtil, times(1)).getUserId("test-token");
        verify(petService, times(1)).deletePet(userId, petId);
    }

    @Test
    @DisplayName("查询宠物列表-带筛选条件")
    void testGetPetListWithFilters() {
        PetQueryDTO dto = new PetQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setCategoryId(1);
        dto.setCity("北京");

        Page<PetListVO> page = new Page<>();
        page.setTotal(5);
        List<PetListVO> records = new ArrayList<>();
        PetListVO vo = new PetListVO();
        vo.setPetId("pet1");
        records.add(vo);
        page.setRecords(records);

        when(petService.getPetList(any(PetQueryDTO.class))).thenReturn(page);

        Result<Page<PetListVO>> result = petController.getPetList(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(5, result.getData().getTotal());
        assertEquals(1, result.getData().getRecords().size());
        verify(petService, times(1)).getPetList(any(PetQueryDTO.class));
    }

    @Test
    @DisplayName("查询我的发布-使用默认分页")
    void testGetMyPetsWithDefaultPaging() {
        String authorization = "Bearer test-token";
        String userId = "user123";

        Page<PetListVO> page = new Page<>();
        page.setTotal(0);
        page.setRecords(new ArrayList<>());

        when(jwtUtil.getUserId(anyString())).thenReturn(userId);
        when(petService.getMyPets(anyString(), anyInt(), anyInt())).thenReturn(page);

        Result<Page<PetListVO>> result = petController.getMyPets(authorization, 1, 10);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(petService, times(1)).getMyPets(userId, 1, 10);
    }
}
