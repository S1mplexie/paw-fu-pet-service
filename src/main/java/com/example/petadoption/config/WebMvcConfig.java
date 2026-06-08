package com.example.petadoption.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC配置 - 静态资源映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.storage.local.path:uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.isAbsolute()) {
            uploadDir = new File(System.getProperty("user.dir"), uploadPath);
        }
        
        String absolutePath = uploadDir.getAbsolutePath();
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath);
    }
}
