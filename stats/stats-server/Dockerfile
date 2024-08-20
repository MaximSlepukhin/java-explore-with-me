FROM amazoncorretto:21-alpine-jdk
RUN -ls
WORKDIR /app
COPY target/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]