FROM sequenceiq/hadoop-docker:2.7.0
MAINTAINER Hashmap Inc, akshay.mhetre@hashmapinc.com

USER root
ARG IGNITE_VERSION=2.3.0
ARG SCALA_VERSION=2.11
ARG JSON4S_AST=3.5.0

RUN yum install -y wget zip unzip java-1.8.0-openjdk-devel

RUN mkdir /opt/ignite

COPY ignite/default-config.xml /opt/ignite/
COPY ignite/properties.prop /opt/ignite/

WORKDIR /opt/ignite
RUN sed -i s/IGNITE_IP/$(hostname -i)/g default-config.xml \
    && wget -q "https://archive.apache.org/dist/ignite/$IGNITE_VERSION/apache-ignite-hadoop-$IGNITE_VERSION-bin.zip" \
    && wget -q "http://central.maven.org/maven2/org/apache/ignite/ignite-yarn/$IGNITE_VERSION/ignite-yarn-$IGNITE_VERSION.jar" \
    && wget -q "http://central.maven.org/maven2/org/json4s/json4s-ast_$SCALA_VERSION/$JSON4S_AST/json4s-ast_$SCALA_VERSION-$JSON4S_AST.jar"

#Following is the CMD command in base image
RUN ["/etc/bootstrap.sh","-bash"]
#Tried : ["/etc/bootstrap.sh","-d"]. This goes into loop

#Following commands require all hdfs and yarn services running
RUN $HADOOP_PREFIX/bin/hadoop fs -put "apache-ignite-hadoop-$IGNITE_VERSION-bin.zip" /ignite \
    && $HADOOP_PREFIX/bin/hadoop fs -put "default-config.xml" /ignite \
    && $HADOOP_PREFIX/bin/hadoop fs -put "json4s-ast_$SCALA_VERSION-$JSON4S_AST.jar" /ignite/libs

COPY igniteservice.sh /opt/ignite
RUN chmod +x /opt/ignite/igniteservice.sh
EXPOSE 47500-47509
CMD ["igniteservice.sh", "start"]

