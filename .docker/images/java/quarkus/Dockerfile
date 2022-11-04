## Stage 1 : build with maven builder image with native capabilities
FROM quay.io/quarkus/ubi-quarkus-native-image:22.2.0-java17 AS build
USER root
RUN microdnf install findutils
COPY --chown=quarkus:quarkus /java/quarkus/gradlew /code/gradlew
COPY --chown=quarkus:quarkus /java/quarkus/gradle /code/gradle
COPY --chown=quarkus:quarkus /java/quarkus/build.gradle /code/
COPY --chown=quarkus:quarkus /java/quarkus/settings.gradle /code/
COPY --chown=quarkus:quarkus /java/quarkus/gradle.properties /code/
USER quarkus
WORKDIR /code
COPY /java/quarkus/src /code/src
RUN ./gradlew build -Dquarkus.package.type=native

## Stage 2 : create the docker final image
FROM quay.io/quarkus/quarkus-micro-image:2.0
WORKDIR /work/
COPY --from=build /code/build/*-runner /work/application
RUN chmod 775 /work
EXPOSE 8000
CMD ["./application"]