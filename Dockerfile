# Use the official Gradle image to create a build artifact.
FROM gradle:jdk21-graal AS build

# Set the working directory.
WORKDIR /home/gradle/src

# Cache dependencies using a named volume.
VOLUME /home/gradle/.gradle

# Copy the source code to the Docker image.
COPY . .

# Build the project.
RUN gradle build --no-daemon -x test

# Use the GraalVM JDK image.
FROM container-registry.oracle.com/graalvm/jdk:21 AS graalvm

# Set the working directory.
WORKDIR /app

# Copy the jar file from the build stage.
COPY --from=build /home/gradle/src/build/libs/website*-standalone.jar ./website.jar

# Expose the port your app runs on
EXPOSE 42081

ENV GITHUB_TOKEN=${GITHUB_TOKEN}
ENV GITHUB_BASE_URL=${GITHUB_BASE_URL}

# Run the application.
CMD ["java", "-jar", "website.jar"]
