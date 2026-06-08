# 宠物领养服务系统

## 项目简介

本项目是一个宠物领养服务平台，用户可以发布宠物领养信息，浏览领养广场，寻找想要领养的宠物并与发布者取得联系。

## 技术栈

### 后端
- Spring Boot 2.3.12
- JDK 1.8
- MyBatis-Plus 3.4.3
- MySQL 5.7
- Redis
- Spring Security + JWT
- Knife4j API文档

### 前端
- Vue 2.6.14
- Element UI 2.15.8
- Vue Router 3.5.3
- Vuex 3.6.2
- Axios

## 项目结构

```
demo1/
├── src/main/
│   ├── java/com/example/petadoption/
│   │   ├── controller/          # 控制器层
│   │   ├── service/             # 服务层
│   │   ├── mapper/              # 数据访问层
│   │   ├── entity/              # 实体类
│   │   ├── dto/                 # 数据传输对象
│   │   ├── vo/                  # 视图对象
│   │   ├── config/              # 配置类
│   │   ├── util/                # 工具类
│   │   ├── exception/           # 异常处理
│   │   └── PetAdoptionApplication.java
│   └── resources/
│       ├── db/schema.sql        # 数据库脚本
│       ├── mapper/              # MyBatis XML映射文件
│       └── application.yml      # 配置文件
├── frontend/                     # 前端项目
│   ├── src/
│   │   ├── views/               # 页面组件
│   │   ├── components/          # 公共组件
│   │   ├── router/              # 路由配置
│   │   ├── store/               # Vuex状态管理
│   │   ├── api/                 # API接口
│   │   ├── utils/               # 工具函数
│   │   └── main.js
│   └── package.json
└── pom.xml
```

## 功能模块

### 已完成模块

1. **数据库设计** ✅
   - 用户表、领养信息表、联系记录表
   - 字典表（宠物种类、宠物状态）
   - 操作日志表
   - 完整的索引设计

2. **后端基础架构** ✅
   - Spring Boot项目搭建
   - 统一响应格式
   - 全局异常处理
   - MyBatis-Plus配置
   - Swagger API文档
   - 跨域配置

3. **用户管理模块** ✅
   - 用户注册（密码BCrypt加密）
   - 用户登录（JWT Token）
   - 用户信息查询
   - 用户信息更新
   - 敏感信息脱敏

4. **JWT认证授权** ✅
   - JWT工具类
   - Token生成和解析
   - Token验证和刷新
   - Spring Security配置

5. **宠物领养信息** ✅
   - 实体类和DTO/VO设计
   - 数据模型定义

6. **前端Vue项目** ✅
   - 项目搭建和配置
   - 路由配置
   - Vuex状态管理
   - Axios请求封装
   - 登录/注册页面
   - 首页（领养广场）
   - 基础样式设计

### 待完成模块

1. **文件上传服务**
   - MinIO/OSS配置
   - 图片上传功能

2. **领养联系模块**
   - 发起联系
   - 联系记录管理

3. **前端其他页面**
   - 宠物详情页
   - 发布领养信息页
   - 我的发布页

## 快速开始

### 后端启动

1. 创建数据库并执行初始化脚本
```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

2. 修改数据库配置（application.yml）
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pet_adoption
    username: root
    password: your_password
```

3. 启动后端服务
```bash
mvn clean install
mvn spring-boot:run
```

4. 访问API文档
```
http://localhost:8080/api/doc.html
```

### 前端启动

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动前端服务
```bash
npm run serve
```

3. 访问前端页面
```
http://localhost:8081
```

## API接口文档

启动后端服务后，访问 http://localhost:8080/api/doc.html 查看完整的API接口文档。

### 主要接口

#### 用户管理
- POST /api/user/register - 用户注册
- POST /api/user/login - 用户登录
- GET /api/user/info - 获取用户信息
- PUT /api/user/update - 更新用户信息

#### 宠物领养
- GET /api/pet/list - 查询领养信息列表
- GET /api/pet/detail/{id} - 查询领养信息详情
- POST /api/pet/publish - 发布领养信息
- GET /api/pet/my-pets - 查询我的发布
- PUT /api/pet/offline/{id} - 下架领养信息
- DELETE /api/pet/{id} - 删除领养信息

## 数据库表结构

### 用户表 (user)
- user_id: 用户唯一标识
- username: 用户名
- password: 密码（BCrypt加密）
- phone: 手机号
- email: 邮箱
- nickname: 昵称
- avatar: 头像
- status: 状态（1-正常，0-禁用）

### 领养信息表 (pet_adoption)
- pet_id: 领养信息唯一标识
- publisher_id: 发布者ID
- pet_name: 宠物名称
- category_id: 宠物种类
- pet_status: 宠物状态
- age: 年龄
- gender: 性别
- description: 详细描述
- photo_urls: 照片URL列表
- province/city/district: 地区信息
- contact_name/phone/wechat: 联系方式
- adoption_status: 领养状态（1-待领养，2-已领养，3-已下架）

## 配置说明

### 后端配置 (application.yml)
- 服务端口：8080
- 数据库：MySQL 5.7
- Redis：localhost:6379
- JWT过期时间：2小时
- 文件上传大小限制：10MB

### 前端配置
- API基础路径：http://localhost:8080/api
- 路由模式：history模式
- 状态管理：Vuex

## 开发规范

### 代码规范
- 使用Lombok简化代码
- 统一使用Result类返回结果
- 使用BusinessException处理业务异常
- Controller层负责参数验证和返回结果
- Service层负责业务逻辑
- Mapper层负责数据访问

### 命名规范
- 类名：大驼峰命名
- 方法名：小驼峰命名
- 常量：全大写下划线分隔
- 数据库表名：小写下划线分隔

## 注意事项

1. **数据库初始化**：首次运行需要执行数据库脚本创建表结构
2. **Redis依赖**：需要安装并启动Redis服务
3. **文件存储**：文件上传功能需要配置MinIO或OSS
4. **JWT密钥**：生产环境请修改JWT密钥配置
5. **跨域配置**：开发环境已配置跨域，生产环境需要调整

## 后续优化建议

1. 完善文件上传功能（MinIO/OSS）
2. 实现领养联系模块
3. 添加Redis缓存优化
4. 完善前端页面（详情页、发布页等）
5. 添加单元测试和集成测试
6. 实现消息通知功能
7. 添加操作日志记录
8. 性能优化（数据库索引、缓存等）

## 联系方式

如有问题或建议，欢迎联系开发团队。
