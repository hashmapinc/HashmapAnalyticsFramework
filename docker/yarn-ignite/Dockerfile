FROM sequenceiq/hadoop-docker:2.7.0
MAINTAINER Hashmap Inc, akshay.mhetre@hashmapinc.com

USER root
ARG IGNITE_VERSION=2.3.0
ARG SCALA_VERSION=2.11
ARG JSON4S_AST=3.5.0

RUN yum history sync

RUN yum install -y wget zip unzip java-1.8.0-openjdk-devel; yum clean all

RUN mkdir /opt/ignite

COPY ignite/default-config.xml /opt/ignite/
COPY ignite/properties.prop /opt/ignite/

WORKDIR /opt/ignite
RUN sed -i s/IGNITE_IP/$(hostname -i)/g default-config.xml \
    && wget -q "https://archive.apache.org/dist/ignite/$IGNITE_VERSION/apache-ignite-hadoop-$IGNITE_VERSION-bin.zip" \
    && wget -q "http://central.maven.org/maven2/org/apache/ignite/ignite-yarn/$IGNITE_VERSION/ignite-yarn-$IGNITE_VERSION.jar" \
    && wget -q "http://central.maven.org/maven2/org/json4s/json4s-ast_$SCALA_VERSION/$JSON4S_AST/json4s-ast_$SCALA_VERSION-$JSON4S_AST.jar"

COPY igniteservice.sh /opt/ignite
RUN chmod +x /opt/ignite/igniteservice.sh
EXPOSE 47500-47509
CMD ["/opt/ignite/igniteservice.sh", "start"]

