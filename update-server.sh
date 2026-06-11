#!/bin/bash
# 本地一键更新服务器脚本

echo "开始更新服务器..."

# 1. 打包更新的文件
echo "打包更新文件..."
tar -czf /tmp/update.tar.gz \
  -C "D:\xm-petService\demo1" \
  frontend/src \
  src

# 2. 上传到服务器
echo "上传到服务器..."
scp /tmp/update.tar.gz ubuntu@43.136.175.87:/tmp/

# 3. 在服务器上应用更新
echo "应用更新..."
ssh ubuntu@43.136.175.87 << 'ENDSSH'
cd /opt/pawfu

# 解压更新
sudo tar -xzf /tmp/update.tar.gz -C /opt/pawfu

# 构建前端
cd frontend
npm run build
sudo rm -rf /var/www/html/*
sudo cp -r dist/* /var/www/html/

# 重启后端
cd /opt/pawfu
sudo pkill -9 -f pet-adoption || true
sleep 2
nohup java -jar target/pet-adoption-*.jar > backend.log 2>&1 &

echo "✓ 更新完成"
ENDSSH

echo "✓ 服务器已更新完成！"
echo "访问: http://43.136.175.87"
