# OPTIMIZED Dockerfile: multi-stage, layered jar, JRE only, non-root.
# Build from the repo root (the parent POM lives there):
#   docker build -f execution-api/Dockerfile -t execution-api:dev .

# ---------- Stage 1: build ----------
# "AS build" names the stage. Nothing from it ends up in the final image
# unless a later stage COPYs it explicitly.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src

# Copy ONLY what decides the dependencies first: wrapper + POMs.
# This layer (and the download below) is reused until a pom.xml changes.
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./
COPY execution-api/pom.xml execution-api/
# The parent lists every module; Maven refuses to start if one module's pom.xml is missing.
COPY provisioning-api/pom.xml provisioning-api/

# Download dependencies into ~/.m2.
# --mount=type=cache keeps ~/.m2 between builds on THIS machine, outside the image,
# so even a pom change only downloads what's new.
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -pl execution-api -am dependency:go-offline -q

# Now the source. Changing Java code invalidates only from here down.
COPY execution-api/src execution-api/src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -pl execution-api -am package -DskipTests -q

# Split the fat jar into folders, one per layer (Spring Boot's "tools" jar mode):
#   dependencies/            third-party jars       (change rarely)
#   spring-boot-loader/      Boot's launcher        (changes on Boot upgrade)
#   snapshot-dependencies/   -SNAPSHOT jars         (change sometimes)
#   application/             our classes + resources (change every commit)
RUN java -Djarmode=tools -jar execution-api/target/execution-api-0.0.1-SNAPSHOT.jar \
    extract --layers --launcher --destination /extracted

# ---------- Stage 2: run ----------
# JRE only: no compiler, no Maven, no source code. Smaller and less to attack.
FROM eclipse-temurin:21-jre
WORKDIR /app

# A plain user with no special rights. If the app is ever exploited,
# the attacker is not root inside the container.
RUN useradd --system --uid 10001 app

# One COPY per layer, least-changing first. Docker caches layer by layer,
# so a code-only change rebuilds and re-pushes just the last one (~tens of KB).
COPY --from=build /extracted/dependencies/ ./
COPY --from=build /extracted/spring-boot-loader/ ./
COPY --from=build /extracted/snapshot-dependencies/ ./
COPY --from=build /extracted/application/ ./

USER app

# Documentation only: tells readers (and tools) the app listens on 8080. It publishes nothing.
EXPOSE 8080

# By default the JVM takes 25% of the container's memory limit as max heap.
# 75% uses the limit better while leaving room for non-heap memory (metaspace, threads).
# JAVA_TOOL_OPTIONS is read by the JVM itself, so it can be overridden at `docker run -e`.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

# The launcher reads the extracted folders as if they were the fat jar.
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
