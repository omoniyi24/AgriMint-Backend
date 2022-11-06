FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app

RUN dos2unix mvnw
RUN mvnw clean install -DskipTests

FROM openjdk:11-slim
COPY --from=build /home/app/target/agri-mint-0.0.1-SNAPSHOT.jar agri-mint-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","agri-mint-0.0.1-SNAPSHOT.jar"]
