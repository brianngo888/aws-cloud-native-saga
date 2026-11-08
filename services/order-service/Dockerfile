FROM gradle:9.2.1-jdk25 AS build
WORKDIR /src
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle --no-daemon clean bootJar
FROM eclipse-temurin:25-jre
RUN groupadd --gid 10001 app && useradd --uid 10001 --gid app app
WORKDIR /app
COPY --from=build /src/build/libs/*.jar app.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
