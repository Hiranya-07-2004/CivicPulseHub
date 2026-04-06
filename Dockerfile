 FROM maven:3.9.6-eclipse-temurin-22 AS build
 COPY . .
 RUN mvn clean package -DskipTests -Dmaven.test.skip=true

 FROM eclipse-temurin:22-jdk
 COPY --from=build /target/*.jar app.jar
 EXPOSE 8081
 ENTRYPOINT ["java", "-jar", "app.jar"]