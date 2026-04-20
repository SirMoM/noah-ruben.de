FROM node:24-bookworm-slim AS node

FROM container-registry.oracle.com/graalvm/jdk:21 AS build-base

WORKDIR /workspace

COPY --from=node /usr/local/ /usr/local/

COPY amper ./amper

RUN chmod +x ./amper

RUN --mount=type=cache,id=noah-ruben-amper,target=/root/.cache/JetBrains/Amper \
    ./amper --version

FROM build-base AS compile

# Copy the source code to the Docker image.
COPY . .

RUN node --version && npm --version

WORKDIR /workspace

RUN --mount=type=cache,id=noah-ruben-amper,target=/root/.cache/JetBrains/Amper \
    --mount=type=cache,id=noah-ruben-build,target=/workspace/build \
    ./amper package -f executable-jar && \
    cp /workspace/build/tasks/*_executableJarJvm/*-jvm-executable.jar /workspace/website.jar

FROM container-registry.oracle.com/graalvm/jdk:21 AS website

WORKDIR /app

ARG OTEL_JAVAAGENT_VERSION=2.23.0

COPY --from=compile /workspace/website.jar ./website.jar
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_JAVAAGENT_VERSION}/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

RUN jar xf ./website.jar && rm ./website.jar

ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

EXPOSE 42081

CMD ["java", "-cp", "BOOT-INF/classes:BOOT-INF/lib/*", "de.noah_ruben.ApplicationKt"]
