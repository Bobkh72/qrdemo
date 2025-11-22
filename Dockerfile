# Use official Eclipse Temurin Java 21 runtime
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn
COPY src src
COPY lib lib

# Make mvnw executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the Spring Boot JAR automatically discovered inside target/
CMD ["sh", "-c", "java -jar $(ls target/*.jar | head -n 1)"]
