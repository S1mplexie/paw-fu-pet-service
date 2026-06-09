# Paw福宠物服务平台 - 云服务器部署方案

## 📋 部署概述

本部署方案适用于云服务器（阿里云、腾讯云、华为云等），包含：
- 一键部署脚本
- Nginx反向代理配置
- 系统服务自启动配置
- 安全配置建议

## 🔧 服务器要求

### 最低配置
- CPU: 2核
- 内存: 4GB
- 硬盘: 40GB
- 带宽: 3Mbps

### 推荐配置
- CPU: 4核
- 内存: 8GB
- 硬盘: 80GB SSD
- 带宽: 5Mbps

### 系统要求
- 操作系统: Ubuntu 20.04/22.04 或 CentOS 7/8
- 需要root权限或sudo权限

## 🚀 快速部署

### 方式一：一键部署脚本

```bash
# 1. 上传部署脚本到服务器
scp deploy.sh root@your-server-ip:/root/

# 2. 执行部署脚本
ssh root@your-server-ip
chmod +x /root/deploy.sh
/root/deploy.sh
```

### 方式二：分步部署

#### 1. 安装基础环境

```bash
# 更新系统
yum update -y  # CentOS
# 或
apt update && apt upgrade -y  # Ubuntu

# 安装必要工具
yum install -y git wget curl vim  # CentOS
# 或
apt install -y git wget curl vim  # Ubuntu
```

#### 2. 安装Docker和Docker Compose

```bash
# 安装Docker
curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun

# 启动Docker
systemctl start docker
systemctl enable docker

# 安装Docker Compose
curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 验证安装
docker --version
docker-compose --version
```

#### 3. 克隆项目代码

```bash
# 创建项目目录
mkdir -p /opt/pawfu
cd /opt/pawfu

# 克隆代码
git clone https://github.com/S1mplexie/paw-fu-pet-service.git .

# 或使用本地上传
# scp -r D:/xm-petService/demo1/* root@your-server-ip:/opt/pawfu/
```

#### 4. 配置环境变量

```bash
# 创建环境变量文件
cat > /opt/pawfu/.env << 'EOF'
# DeepSeek API Key
DEEPSEEK_API_KEY=sk-3255b4516def43a4a99b7d33d889297c

# 数据库配置
DB_ROOT_PASSWORD=pawfu2024
DB_DATABASE=pet_adoption

# Redis密码
REDIS_PASSWORD=pawfu2024

# JWT密钥
JWT_SECRET=pet-adoption-service-secret-key-2024-very-very-long-secret-key-for-security-and-encryption-purpose
EOF
```

#### 5. 启动服务

```bash
cd /opt/pawfu

# 使用Docker Compose启动
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

## 🌐 域名配置（可选）

### 1. 域名解析

在域名服务商控制台添加A记录：
- 主机记录: @ 或 www
- 记录类型: A
- 记录值: 你的服务器IP

### 2. 配置Nginx

```bash
# 修改nginx配置
vim /opt/pawfu/nginx/nginx.conf

# 更新server_name为你的域名
# server_name your-domain.com;

# 重启nginx
docker-compose restart nginx
```

### 3. 配置HTTPS（推荐）

```bash
# 安装certbot
yum install -y certbot python3-certbot-nginx  # CentOS
# 或
apt install -y certbot python3-certbot-nginx  # Ubuntu

# 申请证书
certbot --nginx -d your-domain.com

# 自动续期
crontab -e
# 添加: 0 3 * * * /usr/bin/certbot renew --quiet
```

## 🔐 安全配置

### 1. 防火墙设置

```bash
# 开放必要端口
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --permanent --add-port=22/tcp
firewall-cmd --reload

# 或使用iptables
iptables -A INPUT -p tcp --dport 80 -j ACCEPT
iptables -A INPUT -p tcp --dport 443 -j ACCEPT
iptables -A INPUT -p tcp --dport 22 -j ACCEPT
service iptables save
```

### 2. 修改SSH端口（可选）

```bash
vim /etc/ssh/sshd_config
# 修改: Port 2222
systemctl restart sshd
```

### 3. 禁用root远程登录（可选）

```bash
vim /etc/ssh/sshd_config
# 修改: PermitRootLogin no
systemctl restart sshd
```

## 📊 监控与维护

### 查看服务状态

```bash
# 查看容器状态
docker-compose ps

# 查看资源使用
docker stats

# 查看日志
docker-compose logs -f [service-name]
```

### 数据备份

```bash
# 备份数据库
docker exec mysql-pet mysqldump -uroot -p${DB_ROOT_PASSWORD} pet_adoption > /opt/backup/db_$(date +%Y%m%d).sql

# 备份上传的文件
tar -czf /opt/backup/uploads_$(date +%Y%m%d).tar.gz /opt/pawfu/uploads

# 定时备份（crontab）
crontab -e
# 添加: 0 2 * * * /opt/pawfu/scripts/backup.sh
```

### 更新部署

```bash
cd /opt/pawfu

# 拉取最新代码
git pull

# 重新构建
docker-compose build

# 重启服务
docker-compose up -d
```

## 🔧 性能优化

### 1. MySQL优化

编辑 `docker-compose.yml` 中的MySQL配置：
```yaml
mysql:
  command:
    - --max_connections=500
    - --innodb_buffer_pool_size=1G
    - --query_cache_size=64M
```

### 2. Java应用优化

编辑 `docker-compose.yml` 中的后端配置：
```yaml
backend:
  environment:
    - JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC
```

### 3. Nginx优化

编辑 `nginx/nginx.conf`：
```nginx
worker_processes auto;
worker_connections 1024;
keepalive_timeout 65;
gzip on;
gzip_types text/plain application/json application/javascript text/css;
```

## ❓ 常见问题

### 1. 端口被占用

```bash
# 查看端口占用
netstat -tunlp | grep :80

# 结束占用进程
kill -9 [PID]
```

### 2. 内存不足

```bash
# 查看内存使用
free -h

# 添加swap
dd if=/dev/zero of=/swapfile bs=1M count=2048
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
```

### 3. 数据库连接失败

```bash
# 检查MySQL状态
docker-compose logs mysql

# 重启MySQL
docker-compose restart mysql
```

## 📞 技术支持

- 项目地址: https://github.com/S1mplexie/paw-fu-pet-service
- 问题反馈: GitHub Issues

---

**祝部署顺利！** 🎉
