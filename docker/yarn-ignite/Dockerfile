FROM sequenceiq/hadoop-docker:2.7.0
MAINTAINER Hashmap Inc, akshay.mhetre@hashmapinc.com

USER root
ARG IGNITE_VERSION=2.3.0
ARG SCALA_VERSION=2.11
ARG JSON4S_AST=3.5.0

RUN yum history sync

RUN yum install -y wget zip unzip java-1.8.0-openjdk-devel; yum clean all

RUN mkdir /opt/ignite && mkdir /opt/ignite/user_libs

RUN cd /opt/ && wget -q "http://github.com/downloads/rvoicilas/inotify-tools/inotify-tools-3.14.tar.gz" \
    && tar -xzf inotify-tools-3.14.tar.gz > /dev/null 2>&1 \
    && cd inotify-tools-3.14 \
    && ./configure --prefix=/usr --libdir=/lib64 && make && su -c 'make install' > /dev/null 2>&1

WORKDIR /opt/ignite

RUN wget -q "https://archive.apache.org/dist/ignite/$IGNITE_VERSION/apache-ignite-hadoop-$IGNITE_VERSION-bin.zip" \
    && wget -q "http://central.maven.org/maven2/org/apache/ignite/ignite-yarn/$IGNITE_VERSION/ignite-yarn-$IGNITE_VERSION.jar" \
    && wget -q "http://central.maven.org/maven2/org/json4s/json4s-ast_$SCALA_VERSION/$JSON4S_AST/json4s-ast_$SCALA_VERSION-$JSON4S_AST.jar"

COPY ignite/default-config.xml ignite/properties.prop startup.sh igniteservice.sh /opt/ignite/

RUN chmod +x /opt/ignite/startup.sh /opt/ignite/igniteservice.sh


EXPOSE 47500-47509
CMD ["/opt/ignite/startup.sh","-d"]

