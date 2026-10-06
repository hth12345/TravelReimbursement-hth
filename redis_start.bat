@echo off
chcp 65001 >nul
title Redis Server
echo Starting Redis (foreground mode)...
echo.
echo Press Ctrl+C to stop Redis when done.
echo.

"C:\Program Files\Redis\redis-server.exe"
pause
