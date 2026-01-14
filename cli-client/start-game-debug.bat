@echo off
chcp 65001 >nul
echo ====================================
echo 凡人修仙文字游戏 - 命令行客户端 (调试模式)
echo ====================================
echo.

cd /d "%~dp0"

echo 正在启动游戏客户端（调试模式）...
echo [调试] 将显示所有API请求和响应日志
echo.

set DEBUG=true
mvn exec:java -Dexec.mainClass="com.xiuxian.client.XiuxianGameClient"

pause
