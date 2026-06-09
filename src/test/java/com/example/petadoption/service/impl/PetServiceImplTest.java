package com.example.petadoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petadoption.dto.PetPublishDTO;
import com.example.petadoption.dto.PetQueryDTO;
import com.example.petadoption.entity.PetAdoption;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.mapper.PetMapper;
import com.example.petadoption.vo.PetListVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("宠物服务测试")
public class PetServiceImplTest {

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private PetServiceImpl petService;

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

        Page<PetAdoption> page = new Page<>(1, 10);
        page.setTotal(0);
        page.setRecords(new ArrayList<>());

        when(petMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<PetListVO> result = petService.getPetList(dto);

        assertNotNull(result);
        assertEquals(0, result.getTotal());
        verify(petMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("查询宠物列表-带分类筛选")
    void testGetPetListWithCategory() {
        PetQueryDTO dto = new PetQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setCategoryId(1);

        PetAdoption pet = new PetAdoption();
        pet.setPetId("pet123");
        pet.setPetName("小白");
        pet.setCategoryId(1);
        pet.setPetStatus(1);
        pet.setAdoptionStatus(1);
        pet.setViewCount(10);

        List<PetAdoption> records = new ArrayList<>();
        records.add(pet);

        Page<PetAdoption> page = new Page<>(1, 10);
        page.setTotal(1);
        page.setRecords(records);

        when(petMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<PetListVO> result = petService.getPetList(dto);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        verify(petMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("查询宠物列表-带城市筛选")
    void testGetPetListWithCity() {
        PetQueryDTO dto = new PetQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setCity("北京");

        Page<PetAdoption> page = new Page<>(1, 10);
        page.setTotal(0);
        page.setRecords(new ArrayList<>());

        when(petMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<PetListVO> result = petService.getPetList(dto);

        assertNotNull(result);
        verify(petMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("查询宠物详情成功")
    void testGetPetDetailSuccess() {
        String petId = "pet123";

        PetAdoption pet = new PetAdoption();
        pet.setPetId(petId);
        pet.setPetName("小白");
        pet.setCategoryId(1);
        pet.setPetStatus(1);
        pet.setAdoptionStatus(1);
        pet.setViewCount(10);

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pet);
        when(petMapper.updateById(any(PetAdoption.class))).thenReturn(1);

        PetListVO result = petService.getPetDetail(petId);

        assertNotNull(result);
        assertEquals(petId, result.getPetId());
        assertEquals("小白", result.getPetName());
        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, times(1)).updateById(any(PetAdoption.class));
    }

    @Test
    @DisplayName("查询宠物详情失败-宠物不存在")
    void testGetPetDetailNotFound() {
        String petId = "pet123";

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> petService.getPetDetail(petId));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, never()).updateById(any(PetAdoption.class));
    }

    @Test
    @DisplayName("发布宠物信息成功")
    void testPublishPetSuccess() {
        String userId = "user123";

        PetPublishDTO dto = new PetPublishDTO();
        dto.setPetName("小白");
        dto.setCategoryId(1);
        dto.setPetStatus(1);
        dto.setAge(2);
        dto.setGender(1);
        dto.setColor("白色");
        dto.setWeight("5.5");
        dto.setDescription("一只可爱的小狗");
        dto.setPhotoUrl("http://example.com/photo.jpg");
        dto.setProvince("北京");
        dto.setCity("北京市");
        dto.setDistrict("朝阳区");
        dto.setAddress("某某街道");
        dto.setContactName("张三");
        dto.setContactPhone("13800138000");
        dto.setContactWechat("zhangsan");

        when(petMapper.insert(any(PetAdoption.class))).thenReturn(1);

        assertDoesNotThrow(() -> petService.publishPet(userId, dto));

        verify(petMapper, times(1)).insert(any(PetAdoption.class));
    }

    @Test
    @DisplayName("发布宠物信息-无体重")
    void testPublishPetWithoutWeight() {
        String userId = "user123";

        PetPublishDTO dto = new PetPublishDTO();
        dto.setPetName("小白");
        dto.setCategoryId(1);
        dto.setPetStatus(1);
        dto.setContactName("张三");
        dto.setContactPhone("13800138000");

        when(petMapper.insert(any(PetAdoption.class))).thenReturn(1);

        assertDoesNotThrow(() -> petService.publishPet(userId, dto));

        verify(petMapper, times(1)).insert(any(PetAdoption.class));
    }

    @Test
    @DisplayName("更新宠物信息成功")
    void testUpdatePetSuccess() {
        String userId = "user123";
        String petId = "pet123";

        PetPublishDTO dto = new PetPublishDTO();
        dto.setPetName("小白");
        dto.setCategoryId(1);
        dto.setPetStatus(1);
        dto.setContactName("张三");
        dto.setContactPhone("13800138000");

        PetAdoption pet = new PetAdoption();
        pet.setPetId(petId);
        pet.setPublisherId(userId);
        pet.setPetName("旧名字");
        pet.setAdoptionStatus(1);

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pet);
        when(petMapper.updateById(any(PetAdoption.class))).thenReturn(1);

        assertDoesNotThrow(() -> petService.updatePet(userId, petId, dto));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, times(1)).updateById(any(PetAdoption.class));
    }

    @Test
    @DisplayName("更新宠物信息失败-宠物不存在")
    void testUpdatePetNotFound() {
        String userId = "user123";
        String petId = "pet123";

        PetPublishDTO dto = new PetPublishDTO();
        dto.setPetName("小白");
        dto.setCategoryId(1);
        dto.setPetStatus(1);
        dto.setContactName("张三");
        dto.setContactPhone("13800138000");

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> petService.updatePet(userId, petId, dto));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, never()).updateById(any(PetAdoption.class));
    }

    @Test
    @DisplayName("更新宠物信息失败-非发布者")
    void testUpdatePetNotPublisher() {
        String userId = "user123";
        String petId = "pet123";

        PetPublishDTO dto = new PetPublishDTO();
        dto.setPetName("小白");
        dto.setCategoryId(1);
        dto.setPetStatus(1);
        dto.setContactName("张三");
        dto.setContactPhone("13800138000");

        PetAdoption pet = new PetAdoption();
        pet.setPetId(petId);
        pet.setPublisherId("user456");
        pet.setAdoptionStatus(1);

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pet);

        assertThrows(BusinessException.class, () -> petService.updatePet(userId, petId, dto));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, never()).updateById(any(PetAdoption.class));
    }

    @Test
    @DisplayName("更新宠物信息失败-已领养")
    void testUpdatePetAlreadyAdopted() {
        String userId = "user123";
        String petId = "pet123";

        PetPublishDTO dto = new PetPublishDTO();
        dto.setPetName("小白");
        dto.setCategoryId(1);
        dto.setPetStatus(1);
        dto.setContactName("张三");
        dto.setContactPhone("13800138000");

        PetAdoption pet = new PetAdoption();
        pet.setPetId(petId);
        pet.setPublisherId(userId);
        pet.setAdoptionStatus(2);

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pet);

        assertThrows(BusinessException.class, () -> petService.updatePet(userId, petId, dto));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, never()).updateById(any(PetAdoption.class));
    }

    @Test
    @DisplayName("查询我的发布成功")
    void testGetMyPetsSuccess() {
        String userId = "user123";

        PetAdoption pet = new PetAdoption();
        pet.setPetId("pet123");
        pet.setPublisherId(userId);
        pet.setPetName("小白");
        pet.setCategoryId(1);
        pet.setPetStatus(1);
        pet.setAdoptionStatus(1);

        List<PetAdoption> records = new ArrayList<>();
        records.add(pet);

        Page<PetAdoption> page = new Page<>(1, 10);
        page.setTotal(1);
        page.setRecords(records);

        when(petMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<PetListVO> result = petService.getMyPets(userId, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        verify(petMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("下架宠物信息成功")
    void testOfflinePetSuccess() {
        String userId = "user123";
        String petId = "pet123";

        PetAdoption pet = new PetAdoption();
        pet.setPetId(petId);
        pet.setPublisherId(userId);
        pet.setAdoptionStatus(1);

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pet);
        when(petMapper.updateById(any(PetAdoption.class))).thenReturn(1);

        assertDoesNotThrow(() -> petService.offlinePet(userId, petId));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, times(1)).updateById(any(PetAdoption.class));
    }

    @Test
    @DisplayName("下架宠物信息失败-宠物不存在")
    void testOfflinePetNotFound() {
        String userId = "user123";
        String petId = "pet123";

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> petService.offlinePet(userId, petId));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, never()).updateById(any(PetAdoption.class));
    }

    @Test
    @DisplayName("下架宠物信息失败-非发布者")
    void testOfflinePetNotPublisher() {
        String userId = "user123";
        String petId = "pet123";

        PetAdoption pet = new PetAdoption();
        pet.setPetId(petId);
        pet.setPublisherId("user456");

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pet);

        assertThrows(BusinessException.class, () -> petService.offlinePet(userId, petId));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, never()).updateById(any(PetAdoption.class));
    }

    @Test
    @DisplayName("上架宠物信息成功")
    void testOnlinePetSuccess() {
        String userId = "user123";
        String petId = "pet123";

        PetAdoption pet = new PetAdoption();
        pet.setPetId(petId);
        pet.setPublisherId(userId);
        pet.setAdoptionStatus(3);

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pet);
        when(petMapper.updateById(any(PetAdoption.class))).thenReturn(1);

        assertDoesNotThrow(() -> petService.onlinePet(userId, petId));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, times(1)).updateById(any(PetAdoption.class));
    }

    @Test
    @DisplayName("上架宠物信息失败-状态不允许")
    void testOnlinePetStatusNotAllowed() {
        String userId = "user123";
        String petId = "pet123";

        PetAdoption pet = new PetAdoption();
        pet.setPetId(petId);
        pet.setPublisherId(userId);
        pet.setAdoptionStatus(1);

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pet);

        assertThrows(BusinessException.class, () -> petService.onlinePet(userId, petId));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, never()).updateById(any(PetAdoption.class));
    }

    @Test
    @DisplayName("删除宠物信息成功")
    void testDeletePetSuccess() {
        String userId = "user123";
        String petId = "pet123";

        PetAdoption pet = new PetAdoption();
        pet.setId(1L);
        pet.setPetId(petId);
        pet.setPublisherId(userId);

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pet);
        when(petMapper.deleteById(anyLong())).thenReturn(1);

        assertDoesNotThrow(() -> petService.deletePet(userId, petId));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("删除宠物信息失败-宠物不存在")
    void testDeletePetNotFound() {
        String userId = "user123";
        String petId = "pet123";

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> petService.deletePet(userId, petId));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("删除宠物信息失败-非发布者")
    void testDeletePetNotPublisher() {
        String userId = "user123";
        String petId = "pet123";

        PetAdoption pet = new PetAdoption();
        pet.setPetId(petId);
        pet.setPublisherId("user456");

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pet);

        assertThrows(BusinessException.class, () -> petService.deletePet(userId, petId));

        verify(petMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(petMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("查询宠物详情-增加浏览次数")
    void testGetPetDetailIncreaseViewCount() {
        String petId = "pet123";

        PetAdoption pet = new PetAdoption();
        pet.setPetId(petId);
        pet.setPetName("小白");
        pet.setCategoryId(1);
        pet.setPetStatus(1);
        pet.setViewCount(10);

        when(petMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pet);
        when(petMapper.updateById(any(PetAdoption.class))).thenReturn(1);

        PetListVO result = petService.getPetDetail(petId);

        assertNotNull(result);
        verify(petMapper, times(1)).updateById(argThat(p -> p.getViewCount() == 11));
    }
}
