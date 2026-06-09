# 针对Ubuntu 22.04 + 2GB内存服务器的部署指南

## ⚠️ 重要提醒

你的服务器配置：
- ✅ CPU: 2核（满足要求）
- ⚠️ 内存: 2GB（较低，需要优化）
- ✅ 硬盘: 40GB SSD（满足要求）
- ✅ 带宽: 3Mbps + 200G/月流量（足够）

**建议：添加2GB Swap交换空间**

## 🚀 部署步骤

### 1. 连接服务器

```bash
ssh root@your-server-ip
```

### 2. 添加Swap交换空间（重要！）

```bash
# 创建2GB交换空间
fallocate -l 2G /swapfile
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile

# 永久生效
echo '/swapfile none swap sw 0 0' >> /etc/fstab

# 验证
free -h
```

### 3. 安装Docker

```bash
# 更新系统
apt update && apt upgrade -y

# 安装必要工具
apt install -y curl wget git vim

# 安装Docker
curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun

# 启动Docker
systemctl start docker
systemctl enable docker

# 安装Docker Compose
curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 验证
docker --version
docker-compose --version
```

### 4. 克隆项目

```bash
# 创建目录
mkdir -p /opt/pawfu
cd /opt/pawfu

# 克隆代码
git clone https://github.com/S1mplexie/paw-fu-pet-service.git .

# 或从本地上传（使用scp）
# 本地执行: scp -r D:/xm-petService/demo1/* root@your-server-ip:/opt/pawfu/
```

### 5. 配置环境变量

```bash
cd /opt/pawfu

cat > .env << 'EOF'
# DeepSeek API Key
DEEPSEEK_API_KEY=sk-3255b4516def43a4a99b7d33d889297c

# 数据库配置
DB_ROOT_PASSWORD=pawfu2024secure
DB_DATABASE=pet_adoption

# Redis密码
REDIS_PASSWORD=pawfu2024secure
EOF
```

### 6. 启动服务

```bash
# 使用针对低内存优化的配置
docker-compose -f docker-compose-low-memory.yml up -d

# 查看启动状态
docker-compose -f docker-compose-low-memory.yml ps

# 查看日志
docker-compose -f docker-compose-low-memory.yml logs -f
```

### 7. 配置防火墙

```bash
# 开放端口
ufw allow 80/tcp
ufw allow 443/tcp
ufw allow 22/tcp
ufw --force enable

# 查看状态
ufw status
```

### 8. 验证部署

```bash
# 查看内存使用
free -h

# 查看Docker容器状态
docker ps

# 查看各服务资源使用
docker stats --no-stream
```

## 📊 性能优化说明

### 内存分配（总计2GB）

| 服务 | 内存限制 | 说明 |
|------|----------|------|
| MySQL | 256MB | InnoDB缓冲池 |
| Redis | 128MB | 最大内存限制 |
| Java后端 | 512MB | JVM堆内存 |
| Nginx前端 | ~20MB | 静态文件服务 |
| 系统+其他 | ~400MB | 操作系统开销 |
| **总计** | **~1.3GB** | 剩余700MB缓冲 |

### MySQL优化

- `max_connections=100` - 减少连接数
- `innodb_buffer_pool_size=256M` - 减少缓冲池
- `query_cache_size=0` - 禁用查询缓存（MySQL 8.0默认）

### Java优化

- `-Xms256m -Xmx512m` - 限制堆内存
- `-XX:+UseG1GC` - 使用G1垃圾收集器
- `-XX:MaxGCPauseMillis=200` - 减少GC停顿

### Redis优化

- `maxmemory 128mb` - 最大内存限制
- `maxmemory-policy allkeys-lru` - LRU淘汰策略

## 🔧 常用管理命令

```bash
# 查看服务状态
docker-compose -f docker-compose-low-memory.yml ps

# 查看日志
docker-compose -f docker-compose-low-memory.yml logs -f backend
docker-compose -f docker-compose-low-memory.yml logs -f mysql

# 重启服务
docker-compose -f docker-compose-low-memory.yml restart

# 停止服务
docker-compose -f docker-compose-low-memory.yml stop

# 更新部署
git pull
docker-compose -f docker-compose-low-memory.yml build
docker-compose -f docker-compose-low-memory.yml up -d
```

## 💾 数据备份

```bash
# 创建备份目录
mkdir -p /opt/backup

# 备份数据库
docker exec pawfu-mysql mysqldump -uroot -p${DB_ROOT_PASSWORD} pet_adoption > /opt/backup/db_$(date +%Y%m%d).sql

# 备份上传文件
tar -czf /opt/backup/uploads_$(date +%Y%m%d).tar.gz /opt/pawfu/uploads

# 设置定时备份
crontab -e
# 添加: 0 2 * * * docker exec pawfu-mysql mysqldump -uroot -ppawfu2024secure pet_adoption > /opt/backup/db_$(date +\%Y\%m\%d).sql
```

## 🌐 访问网站

部署完成后：
- **网站地址**: http://your-server-ip
- **管理员账号**: admin
- **管理员密码**: Test1234

## ⚠️ 注意事项

1. **监控内存**
   ```bash
   watch -n 1 free -h
   ```

2. **如果内存不足**
   ```bash
   # 重启服务
   docker-compose -f docker-compose-low-memory.yml restart
   
   # 清理Docker缓存
   docker system prune -a
   ```

3. **流量控制**
   - 200G/月 ≈ 6.7G/天
   - 建议开启Nginx gzip压缩
   - 静态资源设置缓存

## 📈 性能建议

如果访问量增大，建议：
1. 升级到4GB内存
2. 使用CDN加速静态资源
3. 图片使用对象存储（如阿里云OSS）

---

**祝部署顺利！有问题随时反馈。** 🚀
