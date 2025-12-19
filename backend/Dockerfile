# Build Stage
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn/
COPY src ./src
RUN ./mvnw clean package -Dmaven.test.skip=true

# Runtime stage
FROM eclipse-temurin:21-jdk-jammy AS final

EXPOSE 8080

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]