# Step 1: Use Maven to build the app
FROM maven:3.8.5-eclipse-temurin-17 AS build
COPY . .
# We added -Dmaven.test.skip=true to be extra sure it doesn't try to start the DB during build
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Step 2: Use the Temurin Java runtime to run the app
FROM eclipse-temurin:17-jre-focal
COPY --from=build /target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]