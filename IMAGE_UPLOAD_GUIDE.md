# 图片上传功能实现方案

## 一、方案选择

### 存储方式对比

| 方案 | 优点 | 缺点 | 适用场景 |
|------|------|------|---------|
| 本地存储 | 简单、无成本 | 不支持集群、重启丢失 | 单机开发测试 |
| MinIO | 开源、S3兼容、可自建 | 需要部署 | 生产环境 |
| 阿里云OSS | 稳定、快速、CDN | 收费 | 商业项目 |

## 二、推荐方案：本地存储 + Docker MinIO

### 为什么推荐？

1. **开发阶段**：使用本地存储，快速开发
2. **生产阶段**：切换到MinIO，只需改配置
3. **零成本**：MinIO开源免费
4. **易扩展**：后期可迁移到云存储

## 三、实现步骤

### 步骤1：后端实现文件上传接口

#### 1.1 创建文件服务

```java
// FileService.java
public interface FileService {
    String uploadImage(MultipartFile file, String type);
    void deleteFile(String fileUrl);
}
```

#### 1.2 实现本地存储

```java
// LocalFileServiceImpl.java
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileServiceImpl implements FileService {
    
    @Value("${file.storage.local.path:/uploads}")
    private String uploadPath;
    
    @Override
    public String uploadImage(MultipartFile file, String type) {
        // 1. 验证文件类型和大小
        // 2. 生成唯一文件名
        // 3. 保存到本地目录
        // 4. 返回访问URL
    }
}
```

#### 1.3 实现MinIO存储

```java
// MinioFileServiceImpl.java
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "minio")
public class MinioFileServiceImpl implements FileService {
    
    @Autowired
    private MinioClient minioClient;
    
    @Override
    public String uploadImage(MultipartFile file, String type) {
        // 1. 验证文件
        // 2. 上传到MinIO
        // 3. 返回访问URL
    }
}
```

#### 1.4 创建控制器

```java
// FileController.java
@RestController
@RequestMapping("/file")
public class FileController {
    
    @PostMapping("/upload")
    public Result<String> uploadImage(
        @RequestParam("file") MultipartFile file,
        @RequestParam("type") String type // pet/avatar
    ) {
        String url = fileService.uploadImage(file, type);
        return Result.success(url);
    }
}
```

### 步骤2：前端实现上传组件

#### 2.1 封装上传组件

```vue
<!-- ImageUpload.vue -->
<template>
  <el-upload
    :action="uploadUrl"
    :headers="headers"
    :on-success="handleSuccess"
    :before-upload="beforeUpload"
    :show-file-list="false"
  >
    <img v-if="imageUrl" :src="imageUrl" class="avatar">
    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
  </el-upload>
</template>

<script>
export default {
  props: {
    value: String, // 图片URL
    type: String   // pet/avatar
  },
  computed: {
    uploadUrl() {
      return `${baseUrl}/api/file/upload?type=${this.type}`
    }
  }
}
</script>
```

#### 2.2 在表单中使用

```vue
<!-- 发布领养信息 -->
<el-form-item label="宠物照片">
  <image-upload v-model="form.photoUrl" type="pet" />
</el-form-item>

<!-- 个人中心 -->
<el-form-item label="头像">
  <image-upload v-model="form.avatar" type="avatar" />
</el-form-item>
```

### 步骤3：配置静态资源访问

```java
// WebMvcConfig.java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射本地文件目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
```

## 四、配置文件

### application.yml

```yaml
# 文件存储配置
file:
  storage:
    type: local  # local/minio/oss
    local:
      path: uploads
      url-prefix: http://localhost:8080/api/uploads
    minio:
      endpoint: http://localhost:9000
      access-key: minioadmin
      secret-key: minioadmin
      bucket-name: pet-adoption

# 文件上传限制
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB
```

## 五、Docker MinIO部署

### 5.1 启动MinIO容器

```bash
docker run -d \
  --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  -v /data/minio:/data \
  minio/minio server /data --console-address ":9001"
```

### 5.2 访问MinIO控制台

```
http://localhost:9001
用户名：minioadmin
密码：minioadmin
```

### 5.3 创建存储桶

在控制台创建 `pet-adoption` 存储桶，并设置为公开访问。

## 六、完整实现代码

我可以为你实现以下两个方案：

### 方案A：本地存储（快速开始）
- ✅ 保存到项目目录
- ✅ 立即可用
- ⚠️ 重启后文件保留（需配置静态目录）

### 方案B：MinIO存储（生产推荐）
- ✅ 使用Docker部署MinIO
- ✅ 文件永久保存
- ✅ 支持分布式部署

## 七、建议

**开发阶段：**
先使用本地存储快速开发

**上线阶段：**
切换到MinIO或云存储

**迁移成本：**
只需修改配置文件，代码无需改动

---

## 你想选择哪种方案？

1. **本地存储** - 立即实现，无需额外部署
2. **MinIO存储** - 我帮你部署MinIO并实现
3. **两者都实现** - 可配置切换

请告诉我你的选择，我会立即为你实现！
