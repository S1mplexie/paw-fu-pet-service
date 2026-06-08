package com.example.petadoption.controller;

import com.example.petadoption.service.FileService;
import com.example.petadoption.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@Api(tags = "文件上传")
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation("上传图片")
    @PostMapping("/upload")
    public Result<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "pets") String type) {
        String url = fileService.uploadImage(file, type);
        return Result.success(url);
    }

    @ApiOperation("删除图片")
    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@RequestParam("url") String fileUrl) {
        fileService.deleteFile(fileUrl);
        return Result.success();
    }
}
