package com.example.petadoption.controller;

import com.example.petadoption.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@Api(tags = "系统测试")
@RestController
@RequestMapping("/test")
public class TestController {

    @ApiOperation("健康检查")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("message", "宠物领养服务运行正常");
        data.put("timestamp", System.currentTimeMillis());
        return Result.success(data);
    }

    @ApiOperation("欢迎使用")
    @GetMapping("/welcome")
    public Result<String> welcome() {
        return Result.success("欢迎使用宠物领养服务平台！");
    }
}
