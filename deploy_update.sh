#!/bin/bash
# Paw福宠物平台 - 完整部署更新脚本
# 用于获取最新代码并重新部署

set -e

echo "=== 开始部署更新 ==="
echo "时间: $(date)"

# 1. 备份关键文件
echo "[1/7] 备份配置文件和上传目录..."
mkdir -p /opt/pawfu-backups
tar -czf /opt/pawfu-backups/uploads-$(date +%Y%m%d%H%M%S).tar.gz -C /opt/pawfu uploads
cp /opt/pawfu/src/main/resources/application.yml /opt/pawfu-backups/application.yml.bak
echo "✓ 备份完成"

# 2. 删除旧项目并完整克隆
echo "[2/7] 删除旧项目并完整克隆最新代码..."
cd /opt
rm -rf pawfu
git clone https://gitclone.com/github.com/S1mplexie/paw-fu-pet-service.git pawfu
cd pawfu
echo "✓ 克隆完成，当前版本:"
git log -1 --oneline

# 3. 恢复配置文件
echo "[3/7] 恢复配置文件..."
cp /opt/pawfu-backups/application.yml.bak src/main/resources/application.yml
echo "✓ 配置文件已恢复"

# 4. 验证并修复关键配置
echo "[4/7] 验证并修复关键配置..."
# 数据库配置
sed -i 's#jdbc:mysql://localhost:3306/pet_adoption#jdbc:mysql://localhost:3306/pawfu#g' src/main/resources/application.yml
sed -i 's#password: root123456#password: pawfu2024secure#g' src/main/resources/application.yml
# 图片URL前缀
sed -i 's#url-prefix: http://localhost:8080/api/uploads#url-prefix: /api/uploads#g' src/main/resources/application.yml
# DeepSeek API Key
sed -i 's#api-key: your-deepseek-api-key#api-key: sk-3255b4516def43a4a99b7d33d889297c#g' src/main/resources/application.yml
echo "✓ 配置修复完成"

# 5. 构建前端
echo "[5/7] 构建前端..."
cd frontend
npm install --silent
npm run build
echo "✓ 前端构建完成"

# 6. 部署前端到Nginx
echo "[6/7] 部署前端到Nginx..."
rm -rf /var/www/html/*
cp -r dist/* /var/www/html/
echo "✓ 前端部署完成"

# 7. 重启后端服务
echo "[7/7] 重启后端服务..."
cd /opt/pawfu

# 杀死旧的Spring Boot进程
pkill -f "spring-boot:run" || true
sleep 3

# 清理并重新编译
rm -rf target

# 启动后端
nohup mvn spring-boot:run > backend.log 2>&1 &
echo "✓ 后端启动中..."

# 等待后端启动
echo "等待后端启动（最多120秒）..."
for i in {1..24}; do
    sleep 5
    if curl -s http://localhost:8080/api/pets/list > /dev/null 2>&1; then
        echo "✓ 后端启动成功！"
        break
    fi
    echo "  等待中... ($((i*5))秒)"
done

echo ""
echo "=== 部署完成 ==="
echo "前端: http://43.136.175.87"
echo "管理平台: http://43.136.175.87/admin"
echo "API测试: curl http://43.136.175.87/api/pets/list"
echo "日志: /opt/pawfu/backend.log"
