#!/bin/bash

echo "========================================"
echo "  宠物领养服务平台 - 单元测试运行脚本"
echo "========================================"
echo ""

# 检查Maven是否安装
if command -v mvn &> /dev/null; then
    MAVEN_CMD="mvn"
elif [ -f "./mvnw" ]; then
    MAVEN_CMD="./mvnw"
else
    echo "错误：未找到Maven或Maven Wrapper"
    echo "请安装Maven或配置Maven Wrapper"
    exit 1
fi

echo "使用命令: $MAVEN_CMD"
echo ""

# 运行测试
echo "正在运行单元测试..."
echo ""

$MAVEN_CMD clean test

# 检查测试结果
if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "  ✓ 所有测试通过！"
    echo "========================================"
    echo ""
    echo "测试报告位置："
    echo "  - target/surefire-reports/"
    echo ""
else
    echo ""
    echo "========================================"
    echo "  ✗ 测试失败"
    echo "========================================"
    echo ""
    echo "请检查测试报告："
    echo "  - target/surefire-reports/"
    echo ""
    exit 1
fi
