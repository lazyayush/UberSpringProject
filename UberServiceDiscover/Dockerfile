FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY UberServiceDiscover/ .
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]