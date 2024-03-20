#!/bin/bash
set -e

DATE=$(date +%Y%m%d%H%M)
# sh /root/guard.sh -s proxy -d /root/proxy/ -t 1

# 脚本路径
BASE_PATH=$(cd $(dirname $0) && pwd)
# 部署路径
DEPLOY_PATH=${BASE_PATH}/target
# 服务名称
SERVER_NAME="NULL"
# 选择类型 0:检查服务 1:检查和启动服务 2:查看日志 10:停止服务
OPTION_TYPE="0"
JAVA_CMD="java"

# JVM 参数
GC="-XX:+UseBiasedLocking -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:NewRatio=2 -XX:+CMSIncrementalMode -XX:-ReduceInitialCardMarks -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly"
EX="-XX:+OptimizeStringConcat -XX:+DoEscapeAnalysis -XX:+UseNUMA"
HEAP=" -Xms128M -Xmx256M -XX:CompressedClassSpaceSize=512m -XX:MetaspaceSize=800m -XX:MaxMetaspaceSize=800m -XX:MaxDirectMemorySize=512m"
JAVA_OPTIONS="${GC} ${EX} ${HEAP}"


JAR_DIR="jars"
LOG_DIR="logs"

COLOR_CLOSE='\033[0m'           #关闭所有属性
COLOR_HIGH_LIGHT='\033[1m'      #设置高亮度
COLOR_ULINE='\033[0;4m'         #下划线
COLOR_STAR='\033[0;5m'          #闪烁
COLOR_REVERSE='\033[0;7m'       #反显
COLOR_CLEAR='\033[0;8m'         #消隐
COLOR_BLACK='\033[0;30m'        #黑色字
COLOR_RED='\033[0;31m'          #红色字
COLOR_GREEN='\033[0;32m'        #绿色字
COLOR_YELLOW='\033[0;33m'       #黄色字
COLOR_BLUE='\033[0;34m'         #蓝色字
COLOR_PURPLE='\033[0;35m'       #紫色字
COLOR_SKY='\033[0;36m'          #天蓝字
COLOR_WHITE='\033[0;37m'        #白色字

while getopts ":s:d:t:j:" opt
do
    case $opt in
        s)
        SERVER_NAME=${OPTARG}
        ;;
        d)
        DEPLOY_PATH=${OPTARG}
        ;;
        t)
        OPTION_TYPE=${OPTARG}
        ;;
        j)
        JAVA_CMD=$OPTARG
        ;;
        ?)
        echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} unknown shell params $OPTARG"
        ;;
    esac
done

# 查看服务日志
function view_log() {
    server_name=$1
    cd ${DEPLOY_PATH}/${LOG_DIR}
    tail -f ${server_name}.log
}

# 启动单个服务
function start_server() {
    server_name=$1
    if [ ! -f ${DEPLOY_PATH}/${JAR_DIR}/${server_name}.jar ]; then
        echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_GREEN}[start]${COLOR_CLOSE} 不存在${server_name}.jar"
        exit 1
    fi
    if [ ! -d ${DEPLOY_PATH}/${LOG_DIR}/ ]; then
        echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_GREEN}[start]${COLOR_CLOSE} 不存在目录${LOG_DIR} 自动创建"
        mkdir ${LOG_DIR}
    fi
    cd ${DEPLOY_PATH}
    # 开启启动前，打印启动参数
    echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_GREEN}[start]${COLOR_CLOSE} 开始启动 ${server_name}"
    echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_GREEN}[start]${COLOR_CLOSE} JAVA_OPS: $JAVA_OPTIONS"
    # 开始启动
    BUILD_ID=dontKillMe nohup ${JAVA_CMD} -Dfile.encoding=utf-8  -server $JAVA_OPTIONS -jar ${JAR_DIR}/${server_name}.jar > ${LOG_DIR}/${server_name}.log 2>&1 &
    echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_GREEN}[start]${COLOR_CLOSE} 启动 ${server_name} 完成"
}

# 停止单个服务
function stop_server() {
    server_name=$1
    echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_RED}[stop]${COLOR_CLOSE} stop ${server_name}"
    PID=$(ps -ef | grep ${JAR_DIR}/${server_name}.jar | grep -v "grep" | awk '{print $2}')
    if [ -n "$PID" ]; then
        # 正常关闭
        echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_RED}[stop]${COLOR_CLOSE} ${server_name} is running, kill [$PID]"
#        kill -9 $PID
#        echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_RED}[stop]${COLOR_CLOSE} stop ${server_name} success"
        kill -15 $PID
        # 等待最大 30 秒，直到关闭完成。
        for ((i = 0; i < 30; i++))
            do
                sleep 1
                PID=$(ps -ef | grep ${JAR_DIR}/${server_name}.jar | grep -v "grep" | awk '{print $2}')
                if [ -n "$PID" ]; then
                    echo -e ".\c"
                else
                    echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_RED}[stop]${COLOR_CLOSE} stop ${server_name} success"
                    break
                fi
		    done

        # 如果正常关闭失败，那么进行强制 kill -9 进行关闭
        if [ -n "$PID" ]; then
            echo "${COLOR_RED}[stop]${COLOR_CLOSE} server_name failed，force kill -9 $PID"
            kill -9 $PID
        fi
    # 如果 Java 服务未启动，则无需关闭
    else
        echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_RED}[stop]${COLOR_CLOSE} ${server_name} is not run，not need stop"
    fi
}

#检查服务 1:run 0:not run
function check_server() {
    server_name=$1
    exec_stop=$2
    PID=$(ps -ef | grep ${JAR_DIR}/${server_name}.jar | grep -v "grep" | awk '{print $2}')
    #centos 使用这个
    if [ -n "$PID" ]; then
        echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_BLUE}[check]${COLOR_CLOSE} ${server_name} 运行中，PID [${PID}] RES [1]"
        if [ ${exec_stop} == 1 ]; then
            exit 1
        fi
    else
        echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} ${COLOR_BLUE}[check]${COLOR_CLOSE} ${server_name} 未启动, RES [0]"
    fi
    return 0
}

# 检查和启动服务
function check_start() {
    server_name=$1
    check_server ${server_name} 1
#    jar_file=${jar_name}.jar
#    jar_name=$(echo ${jar_file} | awk -F "/"  '{print $NF}'  | awk -F ".jar" '{print $1}')
    start_server ${server_name}
    temp_res=$?
    if [ ${temp_res} = 0 ]; then
        return 0
    fi
}


function help() {
    echo -e "${COLOR_SKY}[wlpiaoyi]-$(date "+%Y.%m.%d-%H.%M.%S")${COLOR_CLOSE} 操作说明:"
    echo -e "${COLOR_YELLOW}帮助: ${COLOR_CLOSE}\t\t\t\tsh ${BASE_PATH}/guard.sh -t 0"
    echo -e "${COLOR_YELLOW}检查和启动服务: ${COLOR_CLOSE}\t\tsh ${BASE_PATH}/guard.sh -s proxy.socket -d /root/proxy/ -t 1"
    echo -e "${COLOR_YELLOW}查看服务日志: ${COLOR_CLOSE}\t\t\tsh ${BASE_PATH}/guard.sh -s proxy.socket -d /root/proxy/ -t 2"
    echo -e "${COLOR_YELLOW}检查服务状态: ${COLOR_CLOSE}\t\t\tsh ${BASE_PATH}/guard.sh -s proxy.socket -d /root/proxy/ -t 3"
    echo -e "${COLOR_YELLOW}停止单个服务: ${COLOR_CLOSE}\t\t\tsh ${BASE_PATH}/guard.sh -s proxy.socket -d /root/proxy/ -t 10"
}

if [ "$OPTION_TYPE" = "0" ]; then
    help
elif [ "$OPTION_TYPE" = "1" ]; then
    check_start ${SERVER_NAME}
elif [ "$OPTION_TYPE" = "2" ]; then
    view_log ${SERVER_NAME}
elif [ "$OPTION_TYPE" = "3" ]; then
    check_server ${SERVER_NAME} 0
elif [ "$OPTION_TYPE" = "10"  ]; then
    stop_server ${SERVER_NAME}
else
  help
fi
