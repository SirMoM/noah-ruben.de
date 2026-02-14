# Use the official Gradle image to create a build artifact.
FROM gradle:jdk21-graal AS build

WORKDIR /workspace


RUN apt-get update && apt-get install -y --no-install-recommends nodejs npm && rm -rf /var/lib/apt/lists/*

# Copy the source code to the Docker image.
COPY . .

RUN chmod +x ./amper ./tailwind/run.sh

WORKDIR /workspace/tailwind

RUN ./run.sh

WORKDIR /workspace

RUN ./amper package -f executable-jar

FROM container-registry.oracle.com/graalvm/jdk:21 AS website

WORKDIR /app

COPY --from=build /workspace/build/tasks/_noah-ruben.de_executableJarJvm/noah-ruben.de-jvm-executable.jar ./website.jar

EXPOSE 42081

CMD ["java", "-jar", "website.jar"]
