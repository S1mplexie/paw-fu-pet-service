@echo off
REM Paw福宠物服务平台启动脚本 (Windows)

REM 设置环境变量
set DEEPSEEK_API_KEY=sk-3255b4516def43a4a99b7d33d889297c

REM 显示环境变量状态
echo ================================
echo 环境变量配置：
echo DEEPSEEK_API_KEY: %DEEPSEEK_API_KEY:~0,10%...
echo ================================

REM 启动Spring Boot应用
mvn spring-boot:run
