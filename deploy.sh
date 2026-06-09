#!/bin/bash

# =====================================================
# Paw福宠物服务平台 - 一键部署脚本
# 适用于: Ubuntu 20.04/22.04, CentOS 7/8
# =====================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检测操作系统
detect_os() {
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        OS=$ID
        VER=$VERSION_ID
    else
        log_error "无法检测操作系统"
        exit 1
    fi
    log_info "检测到操作系统: $OS $VER"
}

# 安装Docker
install_docker() {
    if command -v docker &> /dev/null; then
        log_info "Docker已安装"
        return
    fi
    
    log_info "安装Docker..."
    curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun
    
    systemctl start docker
    systemctl enable docker
    
    log_info "Docker安装完成"
}

# 安装Docker Compose
install_docker_compose() {
    if command -v docker-compose &> /dev/null; then
        log_info "Docker Compose已安装"
        return
    fi
    
    log_info "安装Docker Compose..."
    curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    
    log_info "Docker Compose安装完成"
}

# 创建项目目录
create_project_dir() {
    PROJECT_DIR="/opt/pawfu"
    
    if [ -d "$PROJECT_DIR" ]; then
        log_warn "项目目录已存在: $PROJECT_DIR"
        read -p "是否删除并重新创建? (y/n): " choice
        if [ "$choice" = "y" ]; then
            rm -rf "$PROJECT_DIR"
        else
            exit 0
        fi
    fi
    
    mkdir -p "$PROJECT_DIR"
    log_info "创建项目目录: $PROJECT_DIR"
}

# 配置环境变量
setup_env() {
    log_info "配置环境变量..."
    
    read -p "请输入DeepSeek API Key: " DEEPSEEK_KEY
    read -p "请输入数据库root密码 (默认: pawfu2024): " DB_PASSWORD
    DB_PASSWORD=${DB_PASSWORD:-pawfu2024}
    
    cat > /opt/pawfu/.env << EOF
# DeepSeek API Key
DEEPSEEK_API_KEY=$DEEPSEEK_KEY

# 数据库配置
DB_ROOT_PASSWORD=$DB_PASSWORD
DB_DATABASE=pet_adoption

# Redis密码
REDIS_PASSWORD=$DB_PASSWORD

# JWT密钥
JWT_SECRET=pet-adoption-service-secret-key-2024-very-very-long-secret-key-for-security-and-encryption-purpose
EOF
    
    log_info "环境变量配置完成"
}

# 启动服务
start_services() {
    cd /opt/pawfu
    
    log_info "启动服务..."
    docker-compose up -d
    
    log_info "等待服务启动..."
    sleep 30
    
    docker-compose ps
}

# 配置防火墙
setup_firewall() {
    log_info "配置防火墙..."
    
    if command -v firewall-cmd &> /dev/null; then
        # CentOS firewalld
        systemctl start firewalld
        firewall-cmd --permanent --add-port=80/tcp
        firewall-cmd --permanent --add-port=443/tcp
        firewall-cmd --permanent --add-port=22/tcp
        firewall-cmd --reload
        log_info "防火墙配置完成 (firewalld)"
    elif command -v ufw &> /dev/null; then
        # Ubuntu ufw
        ufw allow 80/tcp
        ufw allow 443/tcp
        ufw allow 22/tcp
        ufw --force enable
        log_info "防火墙配置完成 (ufw)"
    else
        log_warn "未检测到防火墙，请手动配置"
    fi
}

# 显示部署信息
show_info() {
    SERVER_IP=$(curl -s ifconfig.me || echo "your-server-ip")
    
    echo ""
    echo "=========================================="
    echo "  部署完成！"
    echo "=========================================="
    echo ""
    echo "访问地址:"
    echo "  前端: http://$SERVER_IP"
    echo "  后端: http://$SERVER_IP/api"
    echo ""
    echo "默认管理员账号:"
    echo "  用户名: admin"
    echo "  密码: Test1234"
    echo ""
    echo "常用命令:"
    echo "  查看状态: cd /opt/pawfu && docker-compose ps"
    echo "  查看日志: cd /opt/pawfu && docker-compose logs -f"
    echo "  重启服务: cd /opt/pawfu && docker-compose restart"
    echo "  停止服务: cd /opt/pawfu && docker-compose stop"
    echo ""
}

# 主函数
main() {
    echo ""
    echo "=========================================="
    echo "  Paw福宠物服务平台 - 自动部署脚本"
    echo "=========================================="
    echo ""
    
    # 检查是否为root用户
    if [ "$EUID" -ne 0 ]; then
        log_error "请使用root用户或sudo执行此脚本"
        exit 1
    fi
    
    # 执行部署步骤
    detect_os
    install_docker
    install_docker_compose
    create_project_dir
    
    log_warn "请将项目代码上传到 /opt/pawfu 目录"
    read -p "代码已上传完成? (y/n): " uploaded
    if [ "$uploaded" != "y" ]; then
        log_info "请上传代码后重新执行脚本"
        exit 0
    fi
    
    setup_env
    start_services
    setup_firewall
    show_info
}

# 执行主函数
main
