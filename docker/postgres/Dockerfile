FROM postgres:10
MAINTAINER Hashmap Inc, akshay.mhetre@hashmapinc.com

COPY sql/init.sql /docker-entrypoint-initdb.d/
RUN mkdir -p /opt/HAF/workflowdata/ && mkdir -p /opt/HAF/postgresdata/