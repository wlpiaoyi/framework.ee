@echo off & setlocal enabledelayedexpansion
title file.scan
cd %~dp0

java -Dfile.encoding=utf-8 -jar file.scan.jar --spring.config.location=config/application.file.scan.yml

goto end
