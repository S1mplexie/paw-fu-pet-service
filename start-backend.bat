@echo off
chcp 65001
echo 正在启动Paw福宠物服务平台后端...
echo.
echo 请确保:
echo 1. MySQL容器已启动 (docker start mysql-pet)
echo 2. 端口8080未被占用
echo.
pause
cd /d %~dp0
set JAVA_OPTS=-Dfile.encoding=UTF-8 -Dspring.profiles.active=default
if exist target\classes (
    echo 使用已编译的类文件启动...
    java %JAVA_OPTS% -cp "target\classes;target\dependency\*" com.example.petadoption.PetAdoptionApplication
) else (
    echo 请先在IDEA中编译项目，或使用Maven: mvn spring-boot:run
)
pause
