@echo off
chcp 65001 >nul
echo ========================================
echo   宠物领养服务平台 - 单元测试运行脚本
echo ========================================
echo.

REM 检查Maven是否安装
where mvn >nul 2>&1
if %errorlevel% equ 0 (
    set MAVEN_CMD=mvn
) else if exist mvnw.cmd (
    set MAVEN_CMD=mvnw.cmd
) else (
    echo 错误：未找到Maven或Maven Wrapper
    echo 请安装Maven或配置Maven Wrapper
    pause
    exit /b 1
)

echo 使用命令: %MAVEN_CMD%
echo.

REM 运行测试
echo 正在运行单元测试...
echo.

%MAVEN_CMD% clean test

REM 检查测试结果
if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   √ 所有测试通过！
    echo ========================================
    echo.
    echo 测试报告位置：
    echo   - target\surefire-reports\
    echo.
) else (
    echo.
    echo ========================================
    echo   × 测试失败
    echo ========================================
    echo.
    echo 请检查测试报告：
    echo   - target\surefire-reports\
    echo.
    pause
    exit /b 1
)

pause
