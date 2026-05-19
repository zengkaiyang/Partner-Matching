@echo off
chcp 65001 >nul
echo ========================================
echo   Partner-Matching 项目启动脚本
echo ========================================
echo.

:: 检查 Docker 是否运行
echo [1/4] 检查 Docker 状态...
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker 未运行，请先启动 Docker Desktop
    pause
    exit /b 1
)
echo ✅ Docker 运行正常
echo.

:: 启动 Docker 容器
echo [2/4] 启动 MySQL 和 Redis 容器...
cd /d "%~dp0"
docker-compose up -d
if errorlevel 1 (
    echo ❌ Docker 容器启动失败
    pause
    exit /b 1
)
echo ✅ Docker 容器启动成功
echo.

:: 等待容器就绪
echo [3/4] 等待数据库就绪...
timeout /t 10 /nobreak >nul
echo ✅ 数据库已就绪
echo.

:: 提示启动后端和前端
echo [4/4] 请手动启动后端和前端服务：
echo.
echo 📌 启动后端（在新窗口执行）：
echo    cd D:\code\my\Partner-Matching
echo    mvn spring-boot:run
echo.
echo 📌 启动前端（在新窗口执行）：
echo    cd D:\code\my\Partner-Matching-front
echo    npm run dev
echo.
echo ========================================
echo   访问地址：
echo   前端：http://localhost:5173
echo   后端API文档：http://localhost:8080/api/doc.html
echo ========================================
echo.
pause
