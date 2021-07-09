FROM gradle:7.1.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon 

FROM adoptopenjdk/openjdk11:latest

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/invenhelper.jar

ENTRYPOINT ["java","-jar","/app/invenhelper.jar"]