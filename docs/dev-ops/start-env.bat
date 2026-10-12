@echo off
chcp 65001 >nul
echo ========================================
echo  差旅报销系统 - 开发环境一键启动
echo ========================================
echo.

echo [1/2] 启动 MySQL / Redis / RabbitMQ ...
docker compose -f docker-compose-environment.yml up -d

echo.
echo [2/2] 等待服务就绪 ...
timeout /t 10 /nobreak >nul

echo.
echo ========================================
echo  服务地址：
echo    MySQL     : localhost:3306 (root/123456)
echo    Redis     : localhost:6379
echo    RabbitMQ  : http://localhost:15672 (admin/admin123)
echo ========================================
echo.
pause
