#!/bin/bash

# 日志清理脚本

LOG_DIR="/var/log/pawfu"
RETENTION_DAYS=7

echo "开始清理日志..."

# 清理Docker日志
docker ps -aq | xargs -I {} sh -c 'docker logs {} >/dev/null 2>&1 && truncate -s 0 $(docker inspect --format="{{.LogPath}}" {})'

# 清理应用日志
find $LOG_DIR -name "*.log" -mtime +$RETENTION_DAYS -delete

echo "日志清理完成！"
