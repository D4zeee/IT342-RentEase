# Use official OpenJDK 17 base image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory inside container
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw* pom.xml ./
COPY .mvn .mvn

# Download dependencies (cached if pom.xml doesn't change)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copy the entire source code
COPY . .

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Expose port 8080 (the one Spring Boot uses)
EXPOSE 8080

# Run the built jar (update the name based on actual jar file!)
CMD ["java", "-jar", "target/rentease-0.0.1-SNAPSHOT.jar"]
