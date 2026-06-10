# ---- Build stage: compile with the JDK directly (no Maven, no network) ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY src ./src
RUN find src -name "*.java" > sources.txt && \
    mkdir -p out && \
    javac -d out @sources.txt && \
    cp -r src/main/resources/static out/ && \
    mkdir -p out/META-INF && \
    printf 'Main-Class: com.daily.procurement.WebApp\n\n' > out/META-INF/MANIFEST.MF && \
    jar cfm app.jar out/META-INF/MANIFEST.MF -C out .

# ---- Run stage: small JRE image ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/app.jar app.jar
# Free hosts inject the listening port via $PORT; default to 8080 locally.
ENV PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -jar app.jar"]
