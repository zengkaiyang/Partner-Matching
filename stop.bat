@echo off
chcp 65001 >nul
echo ========================================
echo   Partner-Matching 项目停止脚本
echo ========================================
echo.

:: 停止 Docker 容器
echo [1/2] 停止 MySQL 和 Redis 容器...
cd /d "%~dp0"
docker-compose down
if errorlevel 1 (
    echo ❌ Docker 容器停止失败
    pause
    exit /b 1
)
echo ✅ Docker 容器已停止
echo.

echo [2/2] 提示：请手动关闭后端和前端服务窗口
echo.
echo ========================================
echo   所有服务已停止
echo ========================================
echo.
pause
