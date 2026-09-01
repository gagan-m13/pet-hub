@echo off
title PET HUB - Running (In-Memory Dev Mode)
echo ===================================================
echo   Starting PET HUB in Dev Mode (H2 In-Memory)
echo   Storefront: http://localhost:8080
echo   Admin:      http://localhost:8080/admin/dashboard.html
echo ===================================================
call "%~dp0.tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run "-Dspring-boot.run.profiles=dev"
pause
