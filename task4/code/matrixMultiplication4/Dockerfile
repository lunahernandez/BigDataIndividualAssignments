FROM openjdk:17-jdk-slim
WORKDIR /app
COPY pom.xml .
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline
COPY src /app/src
RUN mvn clean package
EXPOSE 5701
ENTRYPOINT ["java", "-jar", "target/matrixMultiplication4-1.0-SNAPSHOT.jar"]
