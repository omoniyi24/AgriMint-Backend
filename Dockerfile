FROM openjdk:11-jdk-alpine

RUN mkdir -p /app
#Set the current working directory inside the image
WORKDIR /app

RUN /bin/sh -c "./mvnw clean install"
ENTRYPOINT ["java","-jar","./target/agri-mint-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080

