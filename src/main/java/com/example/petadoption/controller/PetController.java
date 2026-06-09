package com.example.petadoption.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petadoption.dto.PetPublishDTO;
import com.example.petadoption.dto.PetQueryDTO;
import com.example.petadoption.service.PetService;
import com.example.petadoption.service.ViewStatService;
import com.example.petadoption.util.JwtUtil;
import com.example.petadoption.vo.PetListVO;
import com.example.petadoption.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 宠物领养信息控制器
 */
@Api(tags = "宠物领养")
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ViewStatService viewStatService;

    @ApiOperation("查询领养信息列表")
    @GetMapping("/list")
    public Result<Page<PetListVO>> getPetList(PetQueryDTO dto) {
        Page<PetListVO> page = petService.getPetList(dto);
        return Result.success(page);
    }

    @ApiOperation("查询领养信息详情")
    @GetMapping("/detail/{id}")
    public Result<PetListVO> getPetDetail(@PathVariable String id,
                                          HttpServletRequest request,
                                          @RequestHeader(value = "Authorization", required = false) String authorization) {
        String userId = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            try {
                String token = authorization.replace("Bearer ", "");
                userId = jwtUtil.getUserId(token);
            } catch (Exception e) {
            }
        }
        
        viewStatService.recordView(id, userId, request);
        
        PetListVO vo = petService.getPetDetail(id);
        return Result.success(vo);
    }

    @ApiOperation("发布领养信息")
    @PostMapping("/publish")
    public Result<Void> publishPet(@RequestHeader("Authorization") String authorization,
                                    @Valid @RequestBody PetPublishDTO dto) {
        String token = authorization.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        petService.publishPet(userId, dto);
        return Result.success();
    }

    @ApiOperation("更新领养信息")
    @PutMapping("/update/{id}")
    public Result<Void> updatePet(@RequestHeader("Authorization") String authorization,
                                   @PathVariable String id,
                                   @Valid @RequestBody PetPublishDTO dto) {
        String token = authorization.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        petService.updatePet(userId, id, dto);
        return Result.success();
    }

    @ApiOperation("查询我的发布")
    @GetMapping("/my-pets")
    public Result<Page<PetListVO>> getMyPets(@RequestHeader("Authorization") String authorization,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        String token = authorization.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        Page<PetListVO> page = petService.getMyPets(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("下架领养信息")
    @PutMapping("/offline/{id}")
    public Result<Void> offlinePet(@RequestHeader("Authorization") String authorization,
                                    @PathVariable String id) {
        String token = authorization.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        petService.offlinePet(userId, id);
        return Result.success();
    }

    @ApiOperation("上架领养信息")
    @PutMapping("/online/{id}")
    public Result<Void> onlinePet(@RequestHeader("Authorization") String authorization,
                                   @PathVariable String id) {
        String token = authorization.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        petService.onlinePet(userId, id);
        return Result.success();
    }

    @ApiOperation("删除领养信息")
    @DeleteMapping("/{id}")
    public Result<Void> deletePet(@RequestHeader("Authorization") String authorization,
                                   @PathVariable String id) {
        String token = authorization.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        petService.deletePet(userId, id);
        return Result.success();
    }
}
