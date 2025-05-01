# Use official OpenJDK 17 base image
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy Maven wrapper and POM first to take advantage of Docker cache
COPY mvnw* pom.xml ./
COPY .mvn .mvn

# ✅ Give execute permission to mvnw
RUN chmod +x mvnw

# Download dependencies (optional but speeds up builds if pom.xml unchanged)
RUN ./mvnw dependency:go-offline

# Copy the full project
COPY . .

# ✅ Just to be safe, set permission again (in case file system changes ownership after COPY)
RUN chmod +x mvnw

# Build the Spring Boot app
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/rentease-0.0.1-SNAPSHOT.jar"]

