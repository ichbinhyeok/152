FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY . .

RUN chmod +x gradlew
RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS:--XX:+UseG1GC -Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom} -jar /app/app.jar"]
