@echo off
title PET HUB - Running (MySQL Production Mode)
echo ===================================================
echo   Starting PET HUB with MySQL
echo   Storefront: http://localhost:8080
echo   Admin:      http://localhost:8080/admin/dashboard.html
echo ===================================================
call "%~dp0.tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
pause
