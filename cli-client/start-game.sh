#!/bin/bash

echo "===================================="
echo "凡人修仙文字游戏 - 命令行客户端"
echo "===================================="
echo ""

cd "$(dirname "$0")"

echo "正在启动游戏客户端..."
echo ""

mvn exec:java -Dexec.mainClass="com.xiuxian.client.XiuxianGameClient"
