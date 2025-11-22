FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY src ./src
COPY lib ./lib

# Install Maven in the container
RUN apt-get update && apt-get install -y maven

# Build the Spring Boot project
RUN mvn -q -e -DskipTests package

EXPOSE 8080

# Run the built JAR
CMD ["sh", "-c", "java -jar $(ls target/*.jar | head -n 1)"]
