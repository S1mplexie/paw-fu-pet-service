#!/bin/bash

# 数据备份脚本

BACKUP_DIR="/opt/backup"
DATE=$(date +%Y%m%d_%H%M%S)
PROJECT_DIR="/opt/pawfu"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份数据库
echo "开始备份数据库..."
docker exec pawfu-mysql mysqldump -uroot -p${DB_ROOT_PASSWORD} pet_adoption > $BACKUP_DIR/db_$DATE.sql
echo "数据库备份完成: $BACKUP_DIR/db_$DATE.sql"

# 备份上传文件
echo "开始备份上传文件..."
tar -czf $BACKUP_DIR/uploads_$DATE.tar.gz $PROJECT_DIR/uploads
echo "文件备份完成: $BACKUP_DIR/uploads_$DATE.tar.gz"

# 清理30天前的备份
echo "清理旧备份文件..."
find $BACKUP_DIR -name "*.sql" -mtime +30 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +30 -delete

echo "备份完成！"
