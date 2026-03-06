# Use a lightweight Java 17 runtime (Adjust if you are using Java 21)
FROM eclipse-temurin:17-jre-alpine

# Define the artifact path after running 'mvn clean package'
ARG JAR_FILE=target/*.jar

# Copy the compiled JAR into the container
COPY ${JAR_FILE} app.jar

# Expose the standard Spring Boot port
EXPOSE 8080

# Execute the application
ENTRYPOINT ["java", "-jar", "/app.jar"]