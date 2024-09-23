@echo off & setlocal enabledelayedexpansion
title file.scan
cd %~dp0

@echo off
chcp 65001

set GC=-XX:+UseBiasedLocking -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:NewRatio=2 -XX:+CMSIncrementalMode -XX:-ReduceInitialCardMarks -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly
set EX=-XX:+OptimizeStringConcat -XX:+DoEscapeAnalysis -XX:+UseNUMA
set HEAP=-Xms128M -Xmx512M -XX:CompressedClassSpaceSize=128m -XX:MetaspaceSize=300m -XX:MaxMetaspaceSize=300m -XX:MaxDirectMemorySize=128m

set JAVA_OPTS=-Dfile.encoding=UTF-8 %EX% %HEAP%

SET javaCmd=C:\Program Files\Java\graalvm-jdk-21.0.2+13.1\bin\java

echo "%javaCmd%" %JAVA_OPTS% -jar file.scan.jar --spring.config.location=config/application.file.scan.yml

"%javaCmd%" %JAVA_OPTS% -jar file.scan.jar --spring.config.location=config/application.file.scan.yml

goto end
