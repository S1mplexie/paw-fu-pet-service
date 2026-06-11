#!/bin/bash
# Paw福项目 - 自动化更新脚本
# 解决gitclone.com镜像延迟问题，一键更新服务器代码

set -e

echo "======================================"
echo "  Paw福项目 - 自动化更新脚本"
echo "======================================"
echo ""

BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d%H%M%S)

# 1. 备份当前状态
echo "[1/6] 备份当前代码和配置..."
sudo mkdir -p $BACKUP_DIR
sudo cp /opt/pawfu/src/main/resources/application.yml $BACKUP_DIR/application.yml.bak 2>/dev/null || true
sudo cp /etc/nginx/conf.d/pawfu.conf $BACKUP_DIR/nginx-pawfu.conf.bak 2>/dev/null || true
echo "✓ 备份完成"

# 2. 从本地接收更新文件
echo "[2/6] 等待接收本地更新文件..."
echo "请在本地执行："
echo "  scp -r frontend/src ubuntu@43.136.175.87:/tmp/update/"
echo "  scp -r src ubuntu@43.136.175.87:/tmp/update/"
echo ""
read -p "文件已上传? (y/n): " confirm
if [ "$confirm" != "y" ]; then
    echo "取消更新"
    exit 1
fi

# 3. 应用更新
echo "[3/6] 应用代码更新..."
if [ -d "/tmp/update/frontend" ]; then
    sudo cp -r /tmp/update/frontend/src/* /opt/pawfu/frontend/src/
    echo "✓ 前端代码已更新"
fi
if [ -d "/tmp/update/src" ]; then
    sudo cp -r /tmp/update/src/* /opt/pawfu/src/
    echo "✓ 后端代码已更新"
fi

# 4. 恢复配置文件
echo "[4/6] 恢复配置文件..."
sudo cp $BACKUP_DIR/application.yml.bak /opt/pawfu/src/main/resources/application.yml
echo "✓ 配置已恢复"

# 5. 构建前端
echo "[5/6] 构建前端..."
cd /opt/pawfu/frontend
npm run build
sudo rm -rf /var/www/html/*
sudo cp -r dist/* /var/www/html/
echo "✓ 前端已部署"

# 6. 重启后端
echo "[6/6] 重启后端..."
cd /opt/pawfu
sudo pkill -9 -f pet-adoption || true
sleep 2
mvn clean package -DskipTests -q
nohup java -jar target/pet-adoption-*.jar > backend.log 2>&1 &
echo "✓ 后端已启动"

echo ""
echo "======================================"
echo "  更新完成！"
echo "======================================"
echo "访问: http://43.136.175.87"
