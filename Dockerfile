ARG APP_PATH=applications/app-service

# ---- Build ----
FROM gradle:8-jdk21-jammy AS build
ARG APP_PATH
WORKDIR /src
COPY . .
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN --mount=type=cache,target=/home/gradle/.gradle,id=${APP_PATH} \
    ./gradlew -p "$APP_PATH" clean bootJar --no-daemon -x test


# ---- Runtime ----
FROM eclipse-temurin:21-jre-jammy
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
WORKDIR /app
ARG APP_PATH
RUN useradd -u 1000 -m appuser
COPY enviroments-local/users-keystore.jceks /app/cert/users-keystore.jceks
COPY --from=build --chown=1000:1000 /src/${APP_PATH}/build/libs/*.jar /app/app.jar
USER 1000
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
