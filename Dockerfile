FROM openjdk:8-jre-alpine
COPY target/*.jar /app.jar
CMD ["java", "-jar", "app.jar"]