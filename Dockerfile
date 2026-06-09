# 后端Dockerfile
FROM openjdk:8-jdk-alpine

LABEL maintainer="PawFu Pet Service"

# 设置工作目录
WORKDIR /app

# 安装必要工具
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 复制Maven配置
COPY pom.xml .
COPY src ./src

# 构建应用
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests && \
    mv target/*.jar app.jar && \
    rm -rf target src pom.xml

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
