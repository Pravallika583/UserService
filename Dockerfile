FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/user-service-1.0.0-SNAPSHOT-fat.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
