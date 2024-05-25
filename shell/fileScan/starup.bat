@echo off & setlocal enabledelayedexpansion
title file.scan
cd %~dp0

@echo off
chcp 65001

set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
set GC=-XX:+UseBiasedLocking -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:NewRatio=2 -XX:+CMSIncrementalMode -XX:-ReduceInitialCardMarks -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly
set EX=-XX:+OptimizeStringConcat -XX:+DoEscapeAnalysis -XX:+UseNUMA
set HEAP=-Xms128M -Xmx512M -XX:CompressedClassSpaceSize=64m -XX:MetaspaceSize=300m -XX:MaxMetaspaceSize=300m -XX:MaxDirectMemorySize=64m

java -Dfile.encoding=utf-8 -jar file.scan.jar --spring.config.location=config/application.file.scan.yml

goto end
