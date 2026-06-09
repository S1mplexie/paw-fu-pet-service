# Paw福宠物服务平台 - 环境变量配置说明

## 必需的环境变量

### 1. DeepSeek API Key
用于AI智能客服功能
```bash
export DEEPSEEK_API_KEY=sk-3255b4516def43a4a99b7d33d889297c
```

### 2. 数据库配置（可选，默认使用本地配置）
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=pet_adoption
export DB_USER=root
export DB_PASSWORD=root123456
```

### 3. Redis配置（可选）
```bash
export REDIS_HOST=localhost
export REDIS_PORT=6379
```

## 启动方式

### 开发环境（使用环境变量）
```bash
# 设置环境变量
export DEEPSEEK_API_KEY=sk-3255b4516def43a4a99b7d33d889297c

# 启动应用
mvn spring-boot:run
```

### 生产环境
```bash
# 设置所有环境变量
export DEEPSEEK_API_KEY=your-production-api-key
export SPRING_PROFILES_ACTIVE=prod

# 启动应用
java -jar target/pet-adoption-1.0-SNAPSHOT.jar
```

### Windows环境
```cmd
# 设置环境变量
set DEEPSEEK_API_KEY=sk-3255b4516def43a4a99b7d33d889297c

# 启动应用
mvn spring-boot:run
```

## 注意事项

1. **不要将API Key提交到Git仓库**
2. **生产环境使用不同的API Key**
3. **定期更换API Key以提高安全性**
4. **使用`.env`文件管理环境变量（已添加到.gitignore）**

## 使用.env文件（推荐）

创建`.env`文件：
```env
DEEPSEEK_API_KEY=sk-3255b4516def43a4a99b7d33d889297c
DB_PASSWORD=root123456
```

注意：`.env`文件已添加到`.gitignore`，不会被提交到Git。
