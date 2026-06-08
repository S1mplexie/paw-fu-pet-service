# 快速启动检查清单

## ✅ 问题已修复

已修复Swagger配置问题：
- 将 `@EnableSwagger2` 改为 `@EnableSwagger2WebMvc`
- 添加测试接口 `/test/health` 和 `/test/welcome`
- 配置H2数据库控制台访问权限

## 🚀 现在可以启动项目

### 方法一：使用IDEA（推荐）

#### 1. 打开项目
- IntelliJ IDEA → Open → 选择 `D:/xm-petService/demo1`
- 等待Maven下载依赖（首次约3-5分钟）

#### 2. 运行项目
- 找到文件：`src/main/java/com/example/petadoption/PetAdoptionApplication.java`
- 右键 → Run 'PetAdoptionApplication'

#### 3. 验证启动成功
启动日志显示：
```
Started PetAdoptionApplication in X.XXX seconds (JVM running for X.XXX)
```

#### 4. 测试接口

**方式1：浏览器访问**
```
http://localhost:8080/api/test/health
```
返回：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "status": "UP",
    "message": "宠物领养服务运行正常",
    "timestamp": 1234567890
  }
}
```

**方式2：访问Swagger文档**
```
http://localhost:8080/api/doc.html
```

**方式3：访问H2数据库控制台**
```
http://localhost:8080/api/h2-console
```
- JDBC URL: `jdbc:h2:mem:pet_adoption`
- User Name: `sa`
- Password: （留空）

### 方法二：命令行启动

如果已安装Maven：
```bash
# 进入项目目录
cd D:/xm-petService/demo1

# 编译项目
mvn clean compile

# 启动项目
mvn spring-boot:run
```

## 📋 启动后可测试的功能

### 1. 健康检查
```
GET http://localhost:8080/api/test/health
```

### 2. 用户注册
```
POST http://localhost:8080/api/user/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test1234",
  "phone": "13800138000"
}
```

### 3. 用户登录
```
POST http://localhost:8080/api/user/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test1234"
}
```

### 4. 查看API文档
```
http://localhost:8080/api/doc.html
```

## 🔧 可能遇到的问题

### 问题1：端口8080被占用
**解决**：修改 `application.yml`
```yaml
server:
  port: 8081  # 改为其他端口
```

### 问题2：Maven依赖下载慢
**解决**：配置阿里云镜像
- 找到Maven的settings.xml文件
- 添加镜像配置（参考STARTUP.md）

### 问题3：Java版本不匹配
**解决**：项目使用JDK 1.8编译
- IDEA: File → Project Structure → Project → SDK
- 选择JDK 1.8或JDK 17都可以运行

## 🎯 下一步

启动成功后：

1. **查看API文档**：http://localhost:8080/api/doc.html
2. **测试用户接口**：注册、登录
3. **启动前端**：参考STARTUP.md中的前端启动步骤
4. **查看数据库**：http://localhost:8080/api/h2-console

## 📞 需要帮助？

如果启动遇到问题：
1. 查看控制台日志
2. 检查端口是否被占用
3. 确认Maven依赖已下载完成
4. 查看 STARTUP.md 详细文档

---

**提示**：现在请在IDEA中运行 `PetAdoptionApplication.java` 启动项目！
