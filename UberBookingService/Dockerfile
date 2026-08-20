# Stage 1: publish UberEntityService into a local maven repo inside the build image
FROM eclipse-temurin:21-jdk-alpine AS entity-build
WORKDIR /entity
COPY UberEntityService/ .
RUN chmod +x ./gradlew
RUN ./gradlew publishToMavenLocal --no-daemon

# Stage 2: resolve dependencies (cached separately from source)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY --from=entity-build /root/.m2 /root/.m2
COPY UberBookingService/gradlew .
COPY UberBookingService/gradle gradle
COPY UberBookingService/build.gradle .
COPY UberBookingService/settings.gradle* .
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# Stage 3: copy source, compile
COPY UberBookingService/src src
RUN ./gradlew bootJar --no-daemon

# Stage 4: slim runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 7478
ENTRYPOINT ["java", "-jar", "app.jar"]