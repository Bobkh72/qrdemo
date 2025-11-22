FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY lib ./lib

RUN apt-get update && apt-get install -y maven

# Install Mastercard JAR into Maven repo inside Docker
RUN mvn install:install-file \
     -Dfile=lib/pushpayment-core-sdk-2.1.0.jar \
     -DgroupId=com.mastercard \
     -DartifactId=pushpayment-core-sdk \
     -Dversion=2.1.0 \
     -Dpackaging=jar

RUN mvn -q -e -DskipTests package

EXPOSE 8080

CMD ["sh", "-c", "java -jar $(ls target/*.jar | head -n 1)"]
