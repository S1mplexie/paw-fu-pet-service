package com.example.petadoption.controller;

import com.example.petadoption.service.AdminDashboardService;
import com.example.petadoption.vo.DashboardStatsVO;
import com.example.petadoption.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "管理员仪表盘")
@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @ApiOperation("获取仪表盘统计数据")
    @GetMapping("/stats")
    public Result<DashboardStatsVO> getStats() {
        DashboardStatsVO stats = adminDashboardService.getStats();
        return Result.success(stats);
    }
}
