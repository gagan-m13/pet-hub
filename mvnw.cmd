@echo off
set SCRIPT_DIR=%~dp0
set MVN_CMD="%SCRIPT_DIR%.tools\apache-maven-3.9.9\bin\mvn.cmd"

if exist %MVN_CMD% (
    %MVN_CMD% %*
) else (
    mvn %*
)
