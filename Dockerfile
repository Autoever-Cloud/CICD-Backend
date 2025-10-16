FROM openjdk:21-jdk-slim as builder

WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN ./gradlew dependencies

COPY src src

RUN ./gradlew build -x test

FROM openjdk:21-jre-slim

VOLUME /tmp

COPY --from=builder /workspace/app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

EXPOSE 8080