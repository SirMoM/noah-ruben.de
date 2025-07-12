# Development Guidelines for noah-ruben.de

This document provides essential information for developers working on the noah-ruben.de project.

## Build/Configuration Instructions

### Prerequisites
- JDK 21
- Node.js (for Tailwind CSS processing)
- Gradle (wrapper included in the project)

### Environment Setup
1. Set up the required environment variables:
   ```
   GITHUB_TOKEN=your_github_token
   GITHUB_URL=github_api_url (or use Wiremock for development)
   ```

### Building the Project
1. **Generate Tailwind CSS**:
   The project uses Tailwind CSS for styling. The CSS is generated using a script in the `tailwind` directory.
   ```bash
   cd tailwind
   ./run.sh
   ```
   This script is also automatically executed during the Gradle build process.

2. **Build the project**:
   ```bash
   ./gradlew build
   ```

3. **Run the application**:
   ```bash
   ./gradlew run
   ```

4. **Create a standalone JAR**:
   ```bash
   ./gradlew fatJar
   ```
   This creates a standalone JAR file in the `build/libs` directory with the classifier "standalone".

## Testing Information

### Running Tests
To run all tests:
```bash
./gradlew test
```

### Test Structure
- Tests are located in the `src/test/kotlin` directory
- The project uses JUnit and Kotlin's test library
- Ktor's testing framework is used for testing HTTP endpoints

### Creating New Tests
1. Create a new Kotlin file in the appropriate package under `src/test/kotlin`
2. Use the `@Test` annotation from `kotlin.test` for test methods
3. Use assertions from `kotlin.test` such as `assertEquals`, `assertTrue`, etc.

### Testing with Wiremock
The project uses Wiremock to mock external dependencies, particularly the GitHub API.

1. Start the Wiremock server:
   ```bash
   ./wm.sh
   ```

2. The Wiremock server runs on port 42069 by default
3. Mock responses are defined in JSON files in the `wm/mappings` directory

## Additional Development Information

### Project Structure
- `src/main/kotlin/de/noah_ruben/` - Main application code
- `src/main/resources/` - Static resources and configuration files
- `src/test/kotlin/de/noah_ruben/` - Test code
- `tailwind/` - Tailwind CSS configuration and processing
- `wm/` - Wiremock configuration and mappings

### Key Components
- **Application.kt**: Main application entry point and configuration
- **WiremockClient.kt**: Client for interacting with the GitHub API (or its Wiremock substitute)
- **CssClasses.kt**: Constants for CSS class names used throughout the project

### Code Style
The project uses the Kotlinter plugin for code style enforcement. Run the following to check and format code:
```bash
./gradlew lintKotlin    # Check code style
./gradlew formatKotlin  # Format code
```

### Debugging
- Set the `io.ktor.development` system property to `true` for development mode
- Logging is configured using Logback in `src/main/resources/logback.xml`
