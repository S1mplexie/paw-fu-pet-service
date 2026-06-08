package com.example.petadoption.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.petadoption.exception.BusinessException;
import com.example.petadoption.exception.ErrorCode;
import com.example.petadoption.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 本地文件存储服务实现
 */
@Slf4j
@Service
public class LocalFileServiceImpl implements FileService {

    @Value("${file.storage.local.path:uploads}")
    private String uploadPath;

    @Value("${file.storage.local.url-prefix:http://localhost:8080/api/uploads}")
    private String urlPrefix;

    private static final List<String> ALLOWED_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    public String uploadImage(MultipartFile file, String type) {
        try {
            if (file == null || file.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
            }

            log.info("开始上传文件: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());

            if (file.getSize() > MAX_SIZE) {
                throw new BusinessException(ErrorCode.FILE_SIZE_ERROR);
            }

            String originalFilename = file.getOriginalFilename();
            if (StrUtil.isBlank(originalFilename)) {
                throw new BusinessException(ErrorCode.FILE_TYPE_ERROR);
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!ALLOWED_TYPES.contains(extension)) {
                throw new BusinessException(ErrorCode.FILE_TYPE_ERROR);
            }

            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = IdUtil.fastSimpleUUID() + "." + extension;

            String relativePath = type + "/" + datePath + "/" + fileName;
            String fullPath = uploadPath + "/" + relativePath;

            File destFile = new File(fullPath);
            if (!destFile.getParentFile().exists()) {
                boolean created = destFile.getParentFile().mkdirs();
                log.info("创建目录: {}, 结果: {}", destFile.getParentFile().getAbsolutePath(), created);
            }

            file.transferTo(destFile);
            log.info("文件上传成功: {}", fullPath);
            
            String url = urlPrefix + "/" + relativePath;
            log.info("返回URL: {}", url);
            
            return url;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (StrUtil.isBlank(fileUrl) || !fileUrl.startsWith(urlPrefix)) {
            return;
        }

        String relativePath = fileUrl.substring(urlPrefix.length() + 1);
        String fullPath = uploadPath + "/" + relativePath;

        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
            log.info("文件删除成功: {}", fullPath);
        }
    }
}
