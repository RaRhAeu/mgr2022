FROM gradle:jdk17-alpine as builder

COPY /java/micronaut/gradlew /app/gradlew
COPY /java/micronaut/gradle /app/gradle
COPY /java/micronaut/build.gradle.kts /app/
COPY /java/micronaut/settings.gradle.kts /app/
COPY /java/micronaut/gradle.properties /app/
RUN chmod +x /app/gradlew
WORKDIR /app/
COPY /java/micronaut/src /app/src
RUN ./gradlew clean build


FROM openjdk:17-alpine
WORKDIR /app/
COPY --from=builder /app/build/libs/micronaut-0.1-all.jar /app/micronaut/app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "/app/micronaut/app.jar"]