@echo off
REM 设置控制台编码为 UTF-8
chcp 65001 >nul
cls

echo ====================================
echo 凡人修仙文字游戏 - 命令行客户端
echo ====================================
echo.
echo 控制台编码: UTF-8 (代码页 65001)
echo.

cd /d "%~dp0"

echo 正在启动游戏客户端...
echo.

mvn exec:java -Dexec.mainClass="com.xiuxian.client.XiuxianGameClient" -Dfile.encoding=UTF-8

pause
