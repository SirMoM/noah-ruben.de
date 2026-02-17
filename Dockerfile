FROM node:20-bookworm-slim AS node

FROM container-registry.oracle.com/graalvm/jdk:21 AS build

WORKDIR /workspace


COPY --from=node /usr/local/ /usr/local/

# Copy the source code to the Docker image.
COPY . .

RUN chmod +x ./amper

RUN node --version && npm --version

WORKDIR /workspace

RUN ./amper package -f executable-jar

FROM container-registry.oracle.com/graalvm/jdk:21 AS website

WORKDIR /app

COPY --from=build /workspace/build/tasks/*_executableJarJvm/*-jvm-executable.jar ./website.jar

RUN jar xf ./website.jar && rm ./website.jar

EXPOSE 42081

CMD ["java", "-cp", "BOOT-INF/classes:BOOT-INF/lib/*", "de.noah_ruben.ApplicationKt"]
