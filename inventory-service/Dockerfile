FROM openjdk:8-jre-alpine
RUN apk update && apk add --no-cache gcompat
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]