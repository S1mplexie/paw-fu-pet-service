package com.example.petadoption.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传图片
     * @param file 文件
     * @param type 类型（pets/avatars）
     * @return 图片访问URL
     */
    String uploadImage(MultipartFile file, String type);

    /**
     * 删除文件
     * @param fileUrl 文件URL
     */
    void deleteFile(String fileUrl);
}
