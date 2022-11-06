FROM maven:3.6.3-openjdk-14-slim AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
RUN mvn clean install -DskipTests

FROM openjdk:14-slim
COPY --from=build /workspace/target/agri-mint-0.0.1-SNAPSHOT.jar agri-mint-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","agri-mint-0.0.1-SNAPSHOT.jar"]
