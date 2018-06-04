FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
ARG SERVER_PORT
ADD ${JAR_FILE} app.jar
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]