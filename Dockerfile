FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:14-slim
COPY --from=build /workspace/target/agri-mint-0.0.1-SNAPSHOT.jar agri-mint-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","agri-mint-0.0.1-SNAPSHOT.jar"]
