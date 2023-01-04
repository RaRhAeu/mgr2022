FROM gradle:jdk17-alpine as builder

COPY /java/quarkus/gradlew /app/gradlew
COPY /java/quarkus/gradle /app/gradle
COPY /java/quarkus/build.gradle /app/
COPY /java/quarkus/settings.gradle /app/
COPY /java/quarkus/gradle.properties /app/
RUN chmod +x /app/gradlew
WORKDIR /app/
COPY /java/quarkus/src /app/src
RUN ./gradlew clean build -Dquarkus.profile=dev


FROM openjdk:17-alpine
WORKDIR /app/
COPY --from=builder /app/build/quarkus-app /app/quarkus
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "-Dquarkus.profile=dev", "/app/quarkus/quarkus-run.jar"]