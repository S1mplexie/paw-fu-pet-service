# 宠物领养服务系统 - 快速启动指南

## 环境要求

由于当前环境缺少Maven和Node.js，建议使用以下方式启动：

### 必需软件安装

#### 1. 安装Maven
```bash
# 下载Maven 3.8.6
https://maven.apache.org/download.cgi

# 配置环境变量
MAVEN_HOME = Maven安装路径
Path添加 = %MAVEN_HOME%\bin

# 验证安装
mvn -v
```

#### 2. 安装Node.js
```bash
# 下载Node.js 16.x LTS
https://nodejs.org/

# 验证安装
node -v
npm -v
```

#### 3. IDE安装（推荐）
- IntelliJ IDEA（后端开发）
- VS Code（前端开发）

## 方案一：使用IDE启动（推荐）

### 启动后端服务

#### 1. 使用IntelliJ IDEA
1. 打开IDEA，选择 `Open` 打开项目根目录 `D:/xm-petService/demo1`
2. 等待Maven自动下载依赖（首次需要几分钟）
3. 找到主类 `PetAdoptionApplication.java`
   ```
   src/main/java/com/example/petadoption/PetAdoptionApplication.java
   ```
4. 右键点击 → `Run 'PetAdoptionApplication'`
5. 等待启动成功，看到日志：
   ```
   Started PetAdoptionApplication in X.XXX seconds
   ```

#### 2. 启动成功后可访问
- API文档：http://localhost:8080/api/doc.html
- H2数据库控制台：http://localhost:8080/api/h2-console
  - JDBC URL: jdbc:h2:mem:pet_adoption
  - User: sa
  - Password: （空）

### 启动前端服务

#### 1. 使用VS Code
1. 打开VS Code，选择 `File → Open Folder`
2. 打开前端目录：`D:/xm-petService/demo1/frontend`
3. 打开终端：`Terminal → New Terminal`
4. 安装依赖：
   ```bash
   npm install
   ```
5. 启动前端：
   ```bash
   npm run serve
   ```
6. 访问前端：http://localhost:8081

## 方案二：命令行启动

### 后端启动步骤

```bash
# 1. 进入项目目录
cd D:/xm-petService/demo1

# 2. 安装依赖并编译（首次执行）
mvn clean install -DskipTests

# 3. 启动后端服务
mvn spring-boot:run

# 或使用jar包启动
java -jar target/pet-adoption-1.0-SNAPSHOT.jar
```

### 前端启动步骤

```bash
# 1. 进入前端目录
cd D:/xm-petService/demo1/frontend

# 2. 安装依赖
npm install

# 3. 启动前端服务
npm run serve

# 4. 构建生产版本
npm run build
```

## 方案三：Docker启动（如果Docker可用）

```bash
# 1. 启动MySQL和Redis
docker-compose up -d

# 2. 等待容器启动
docker-compose ps

# 3. 按方案一或方案二启动应用
```

## 项目配置说明

### 当前配置（H2内存数据库）
- **优点**：无需安装MySQL，启动快速
- **缺点**：数据不持久化，重启后数据丢失
- **适用场景**：开发测试、功能演示

### 生产环境配置（MySQL）
如需使用MySQL，修改 `application.yml`：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/pet_adoption?useUnicode=true&characterEncoding=utf8mb4
    username: root
    password: root
```

并执行数据库脚本：
```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

## 功能测试流程

### 1. 测试后端API

#### 访问Swagger文档
```
http://localhost:8080/api/doc.html
```

#### 测试用户注册
```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test1234",
    "phone": "13800138000",
    "email": "test@example.com"
  }'
```

#### 测试用户登录
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test1234"
  }'
```

### 2. 测试前端功能

1. 打开浏览器访问：http://localhost:8081
2. 点击"注册"按钮，注册新用户
3. 使用注册的账号登录
4. 浏览领养广场
5. 测试发布领养信息（需登录）

## 常见问题

### 1. Maven依赖下载失败
**解决方案**：
- 检查网络连接
- 配置阿里云Maven镜像（修改settings.xml）
- 使用IDE的Maven工具重新导入

### 2. 前端npm install失败
**解决方案**：
- 清除缓存：`npm cache clean --force`
- 使用淘宝镜像：`npm install --registry=https://registry.npm.taobao.org`
- 删除node_modules后重试

### 3. 端口被占用
**解决方案**：
- 后端：修改 `application.yml` 中的 `server.port`
- 前端：Vue会自动使用下一个可用端口

### 4. Java版本问题
**解决方案**：
- 项目要求JDK 1.8
- 如使用JDK 17，在IDEA中设置项目SDK
- File → Project Structure → Project → SDK

### 5. H2数据库连接失败
**解决方案**：
- 访问 http://localhost:8080/api/h2-console
- JDBC URL填写：jdbc:h2:mem:pet_adoption
- User: sa，Password留空

## 开发建议

### 1. 使用IDE的快捷功能
- IDEA：Live Templates快速生成代码
- VS Code：Vetur插件增强Vue开发体验
- 使用IDE的调试功能断点调试

### 2. 查看日志
- 后端日志：IDE控制台或logs目录
- 前端日志：浏览器控制台（F12）

### 3. 数据查看
- H2控制台：http://localhost:8080/api/h2-console
- 可直接执行SQL查询数据

## 下一步

启动成功后，你可以：

1. **查看API文档**：http://localhost:8080/api/doc.html
2. **测试接口**：使用Swagger或Postman
3. **开发新功能**：参考tasks.md继续开发
4. **前端开发**：完善宠物详情、发布等页面

## 项目结构

```
demo1/
├── src/main/java/com/example/petadoption/  # 后端代码
│   ├── controller/                         # 控制器
│   ├── service/                            # 服务层
│   ├── mapper/                             # 数据访问
│   └── entity/                             # 实体类
├── src/main/resources/
│   ├── application.yml                     # 配置文件
│   └── db/                                 # 数据库脚本
├── frontend/                               # 前端项目
│   ├── src/
│   │   ├── views/                          # 页面
│   │   ├── api/                            # 接口
│   │   └── router/                         # 路由
│   └── package.json                        # 依赖配置
└── README.md                               # 项目说明
```

## 联系支持

如遇到问题，请检查：
1. README.md文档
2. 日志输出
3. 浏览器控制台
4. H2数据库控制台

祝你开发顺利！
