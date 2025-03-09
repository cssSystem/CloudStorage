FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY target/CloudStorage-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]