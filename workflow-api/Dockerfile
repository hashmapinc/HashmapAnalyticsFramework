FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
ARG SERVER_PORT
ARG WORKFLOW_INSTALL_DIR
ADD ${WORKFLOW_INSTALL_DIR} /home/sql
ADD ${JAR_FILE} app.jar
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]