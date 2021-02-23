# Use the official maven/Java 8 image to create a build artifact.
FROM maven:3.5-jdk-8-alpine as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

FROM openjdk:8-jre-alpine

# Copy the jar to the production image
COPY --from=builder /app/target/hello-spring-boot-docker-*.jar /hello-spring-boot-docker.jar

#CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/hello-spring-boot-docker.jar"]
CMD ["java", "-jar", "/hello-spring-boot-docker.jar"]
