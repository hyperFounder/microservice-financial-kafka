# Use a lightweight Java 17 runtime
FROM eclipse-temurin:17-jre-alpine

# Define the artifact path after running 'mvn clean package'
ARG JAR_FILE=target/*.jar

# Copy the compiled JAR into the container
COPY ${JAR_FILE} app.jar

# Expose the Spring Boot port
EXPOSE 8082

# Execute the application
ENTRYPOINT ["java","-jar","/app.jar"]