FROM openjdk:11
ARG JAR_FILE=./target/agri-mint-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} agri-mint-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/agri-mint-0.0.1-SNAPSHOT.jar"]

