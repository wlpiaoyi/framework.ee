@echo off & setlocal enabledelayedexpansion
title file.scan
cd %~dp0

@echo off
chcp 65001

set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8

java -Dfile.encoding=utf-8 -jar file.scan.jar --spring.config.location=config/application.file.scan.yml

goto end
