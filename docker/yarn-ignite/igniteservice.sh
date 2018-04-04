#!/bin/bash
#set -x # echo commands
COMMAND="${1}"
IGNITE_VERSION="2.3.0"
IGNITE_HOME="/opt/ignite"
PROP_FILE="$IGNITE_HOME/properties.prop"
APP_ID_FILE="$IGNITE_HOME/.applicationId"
USER_LIBS="$IGNITE_HOME/user_libs"
IGNITE_VERSION="2.3.0"
IGNITE_YARN_JAR=${IGNITE_HOME}/ignite-yarn-${IGNITE_VERSION}.jar
SCALA_VERSION="2.11"
JSON4S_AST="3.5.0"

function start() {
    if [[ -s ${APP_ID_FILE} ]];
    then
        echo "Previous ignite cluster is already running. Please stop and then start a new one."
    fi
    echo "Starting Ignite cluster"
    `chmod +x ${IGNITE_YARN_JAR}`
    $HADOOP_PREFIX/bin/yarn jar ${IGNITE_YARN_JAR} ${IGNITE_YARN_JAR} ${PROP_FILE} &> temp.txt
    APP_ID=`cat temp.txt | grep 'Application id:' | awk '{print $6}'`
    `rm temp.txt`
    if [[ ${APP_ID} == application_* ]];
    then
        echo ${APP_ID} > ${APP_ID_FILE}
        echo "Ignite cluster started with id: ${APP_ID}"

    else
        echo "Something went wrong. Please Try again later"
    fi
}

function stop() {
    if [[ -s ${APP_ID_FILE} ]];
    then
        echo "Stopping Ignite cluster"
        APP_ID=`cat ${APP_ID_FILE}`
        `$HADOOP_PREFIX/bin/yarn application -kill ${APP_ID} &> temp.txt`
        if [[ `grep 'Killed application' temp.txt | wc -l` == 1 ]];
        then
            `rm temp.txt`
            `rm ${APP_ID_FILE} && touch ${APP_ID_FILE}`
            echo "Ignite Stopped successfully"
        else
            echo "Error while Stopping ignite"
        fi
    else
        echo "Ignite is not running"
    fi
}

function status() {
    if [[ -s ${APP_ID_FILE} ]];
    then
        echo "Ignite Running"
        exit 1
    else
        echo "Ignite not Running"
        exit 1
    fi
}

function deploy() {
    `$HADOOP_PREFIX/bin/hadoop fs -put -f ${USER_LIBS}/ /ignite/libs`
    restart
}

function restart() {
    if [[ -s ${APP_ID_FILE} ]];
    then
        stop
        start
    else
        start
    fi
}

case ${COMMAND} in
    start) start
    ;;
    stop) stop
    ;;
    status) status
    ;;
    deploy) deploy
    ;;
    restart) restart
    ;;
    *) echo "Usage: $0 {start|stop|status|restart|deploy}"
    ;;
esac

