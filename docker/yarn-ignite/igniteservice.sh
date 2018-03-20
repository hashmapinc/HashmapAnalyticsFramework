#!/bin/bash
set -x # echo commands
COMMAND="${1}"
IGNITE_VERSION="2.3.0"
PROP_FILE="properties.prop"
APP_ID_FILE=".applicationId"
IGNITE_HOME="/opt/ignite/"
function init() {
    `cd ${HADOOP_PREFIX}/bin/`
}

function start() {
    if [[ -s ${APP_ID_FILE} ]];
    then
        echo "Previous ignite cluster is already running. Please stop and then start a new one."
        exit 1
    fi

    echo "Starting Ignite cluster"
    IGNITE_YARN_JAR=`${IGNITE_HOME}/ignite-yarn-${IGNITE_VERSION}.jar`
    `yarn jar ${IGNITE_YARN_JAR} ./ ${IGNITE_YARN_JAR} &> temp.txt`
    APP_ID=`cat temp.txt | grep 'Application id:' | awk '{print $6}'`
    `rm temp.txt`
    if [[ ${APP_ID} == application_* ]];
    then
        echo ${APP_ID} > ${APP_ID_FILE}
        echo "Ignite cluster started with id: ${APP_ID}"

    else
        echo "Something went wrong. Please Try again later"
        exit 1
    fi

}

function stop() {
    if [[ -s ${APP_ID_FILE} ]];
    then
        echo "Stopping Ignite cluster"
        APP_ID=`cat ${APP_ID_FILE}`
        `./yarn application -kill ${APP_ID} &> temp.txt`
        if [[ `grep 'Killed application' temp.txt | wc -l` == 1 ]];
        then
            `rm temp.txt`
            `rm ${APP_ID_FILE} && touch ${APP_ID_FILE}`
            echo "Ignite Stopped successfully"
            exit 0
        else
            echo "Error while Stopping ignite"
            exit 1
        fi
    else
        echo "Ignite is not running"
        exit 1
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

init
case ${COMMAND} in
    start) start
    ;;
    stop) stop
    ;;
    status) status
    ;;
#    restart) stop
#             start
#    ;;
esac

