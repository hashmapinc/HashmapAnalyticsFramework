#!/bin/bash
#set -x # echo commands
COMMAND="${1}"
IGNITE_VERSION="2.3.0"
PROP_FILE="properties.prop"
IGNITE_HOME="/opt/ignite"
IGNITE_VERSION="2.3.0"
SCALA_VERSION="2.11"
JSON4S_AST="3.5.0"

#yum update -y libselinux
`/etc/bootstrap.sh`

`$HADOOP_PREFIX/bin/hadoop dfsadmin -safemode leave`

#Creating folders to store ignite related files on HDFS
`$HADOOP_PREFIX/bin/hadoop fs -mkdir /ignite`
`$HADOOP_PREFIX/bin/hadoop fs -mkdir /ignite/libs`

#For ignite 2.3.0 version, there is a dependency on json4s-ast 3.5.0. So adding the at appropriate place
if [[ ${IGNITE_VERSION} == "2.3.0" ]]; then
    `cd ${IGNITE_HOME}`
    `unzip "apache-ignite-hadoop-$IGNITE_VERSION-bin.zip" > /dev/null 2>&1`
    #`rm -rf "$IGNITE_HOME/apache-ignite-hadoop-$IGNITE_VERSION-bin.zip"`
    #`ls -l`
    `cp "json4s-ast_$SCALA_VERSION-$JSON4S_AST.jar" "apache-ignite-hadoop-$IGNITE_VERSION-bin/libs/ignite-spark/"`
    #`ls -l "$IGNITE_HOME/apache-ignite-hadoop-$IGNITE_VERSION-bin/libs/ignite-spark/"`
    `zip -r "apache-ignite-hadoop-$IGNITE_VERSION-bin.zip" "apache-ignite-hadoop-$IGNITE_VERSION-bin/" > /dev/null 2>&1`
fi

#Put current container's IP address
sed -i s/IGNITE_IP/$(hostname -i)/g /opt/ignite/default-config.xml

#Moving required files to HDFS
`$HADOOP_PREFIX/bin/hadoop fs -put "$IGNITE_HOME/apache-ignite-hadoop-$IGNITE_VERSION-bin.zip" /ignite`
`$HADOOP_PREFIX/bin/hadoop fs -put "$IGNITE_HOME/default-config.xml" /ignite`
`$HADOOP_PREFIX/bin/hadoop fs -put "$IGNITE_HOME/json4s-ast_$SCALA_VERSION-$JSON4S_AST.jar" /ignite/libs`
`$HADOOP_PREFIX/bin/hadoop fs -put ${IGNITE_HOME}/${PROP_FILE} /ignite/`
`$HADOOP_PREFIX/bin/hadoop fs -put ${IGNITE_HOME}/ignite-yarn-${IGNITE_VERSION}.jar /ignite/`


`${IGNITE_HOME}/igniteservice.sh start`

sleep 5

while true;
do
    `inotifywait ${IGNITE_HOME}/user_libs && ${IGNITE_HOME}/igniteservice.sh deploy`
done