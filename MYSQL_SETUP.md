# MySQL数据库安装与配置指南

## 一、安装MySQL

### Windows系统安装MySQL

#### 方法一：使用MySQL Installer（推荐）

1. **下载MySQL Installer**
   - 访问官网：https://dev.mysql.com/downloads/installer/
   - 选择 `mysql-installer-community-8.0.x.x.msi`（约400MB）
   - 点击 Download，无需登录直接下载

2. **安装步骤**
   ```
   1. 双击安装包运行
   2. Choosing a Setup Type：选择 "Server only"
   3. 点击 Next → Execute
   4. 等待安装完成
   
   5. Config Type：选择 "Development Computer"
   6. 设置 root 密码（记住这个密码！）
      - 例如：root123456
   7. 点击 Next → Execute
   8. 等待配置完成
   ```

3. **验证安装**
   ```cmd
   # 打开命令提示符
   mysql -u root -p
   # 输入密码，看到 mysql> 提示符表示成功
   ```

#### 方法二：使用Chocolatey（快速）

```powershell
# 以管理员身份打开PowerShell
choco install mysql -y

# 安装后设置密码
mysql -u root
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root123456';
```

### 方法三：使用Docker（已安装Docker的情况）

```bash
# 启动MySQL容器
docker run -d \
  --name mysql-pet \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root123456 \
  -e MYSQL_DATABASE=pet_adoption \
  mysql:8.0

# 查看容器状态
docker ps
```

## 二、创建数据库和用户

### 1. 登录MySQL
```bash
mysql -u root -p
# 输入root密码
```

### 2. 创建数据库和用户
```sql
-- 创建数据库
CREATE DATABASE pet_adoption DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建专用用户（可选）
CREATE USER 'petuser'@'localhost' IDENTIFIED BY 'pet123456';
GRANT ALL PRIVILEGES ON pet_adoption.* TO 'petuser'@'localhost';
FLUSH PRIVILEGES;

-- 查看数据库
SHOW DATABASES;
```

## 三、配置项目连接MySQL

### 1. 修改 application.yml

打开文件：`src/main/resources/application.yml`

将以下内容：
```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:pet_adoption;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false
    username: sa
    password:
    schema: classpath:db/schema-h2.sql
    data: classpath:db/test-data.sql
    initialization-mode: always
```

改为：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/pet_adoption?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root123456  # 改成你的MySQL密码
```

### 2. 注释或删除H2相关配置

```yaml
# 注释掉H2控制台配置
# h2:
#   console:
#     enabled: true
#     path: /h2-console
```

## 四、初始化数据库

### 方式一：自动初始化（推荐）

修改 `application.yml`：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/pet_adoption?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root123456
    schema: classpath:db/schema.sql  # 使用MySQL版本的SQL
    initialization-mode: always
```

### 方式二：手动执行SQL

```bash
# 在项目根目录执行
mysql -u root -p pet_adoption < src/main/resources/db/schema.sql
```

## 五、启动测试

### 1. 重启后端
```
IDEA → 停止服务 → 重新运行 PetAdoptionApplication
```

### 2. 查看日志
启动成功应该看到：
```
Started PetAdoptionApplication in X.XXX seconds
```

### 3. 测试数据持久化
```
1. 登录系统
2. 发布一条领养信息
3. 重启后端
4. 刷新前端，数据应该还在
```

## 六、常见问题

### Q1: 连接被拒绝
```
错误：Communications link failure

解决：
1. 确认MySQL服务已启动
2. Windows: services.msc → 找到MySQL → 启动
3. Docker: docker start mysql-pet
```

### Q2: 密码错误
```
错误：Access denied for user 'root'@'localhost'

解决：
1. 确认密码正确
2. 重置密码：
   mysql -u root
   ALTER USER 'root'@'localhost' IDENTIFIED BY '新密码';
```

### Q3: 数据库不存在
```
错误：Unknown database 'pet_adoption'

解决：
CREATE DATABASE pet_adoption DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Q4: 时区错误
```
错误：The server time zone value 'XXX' is unrecognized

解决：
在URL中添加：serverTimezone=Asia/Shanghai
```

## 七、数据库管理工具（可选）

### 推荐工具：
1. **MySQL Workbench**（官方，免费）
   - 下载：https://dev.mysql.com/downloads/workbench/

2. **Navicat**（收费，有试用版）
   - 官网：https://www.navicat.com/

3. **DBeaver**（免费开源）
   - 下载：https://dbeaver.io/download/

4. **DataGrip**（JetBrains，收费）
   - 与IDEA同公司，体验好

## 八、配置文件示例

完整的MySQL配置示例：

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: pet-adoption-service
  
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/pet_adoption?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root123456
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

# 其他配置保持不变...
```

---

## 快速开始步骤

1. ✅ 下载并安装MySQL
2. ✅ 创建数据库 `pet_adoption`
3. ✅ 修改 `application.yml` 配置
4. ✅ 重启后端服务
5. ✅ 测试数据持久化

安装完成后告诉我，我会帮你修改配置文件！
