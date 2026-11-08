FROM gradle:8.14-jdk25 AS build
WORKDIR /src
COPY . .
RUN gradle --no-daemon clean bootJar
FROM eclipse-temurin:25-jre
RUN groupadd -r app && useradd -r -g app app
WORKDIR /app
COPY --from=build /src/build/libs/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
