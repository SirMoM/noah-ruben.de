# Development Guidelines for noah-ruben.de

This document provides essential information for developers working on the noah-ruben.de project.

## Build and Configuration

### Prerequisites

- JDK 21
- Node.js and npm (required for Tailwind generation during Amper tasks)
- Docker (optional, for containerized runs)
- Tilt (optional, for docker orchestration in `docker/`)

### Environment Setup

Set runtime environment variables as needed:

```bash
GITHUB_TOKEN=your_github_token
GITHUB_URL=github_api_url_or_wiremock_url
```

For local development with Wiremock, `GITHUB_URL` is commonly `http://localhost:42069`.

### Build and Run Commands

Use Amper commands from repository root:

```bash
./amper build
./amper test
./amper run
./amper package -f executable-jar
```

Tailwind generation is integrated via the repo-local Amper plugin (`tailwind/`). No standalone `tailwind/run.sh` step is used.

## Testing Information

### Running Tests

Run all tests:

```bash
./amper test
```

Run a single test class:

```bash
./amper test --include-classes="de.noah_ruben.site.LandingPageTest"
```

Run a single test method:

```bash
./amper test --include-test="de.noah_ruben.site.LandingPageTest.landingPage"
```

### Testing with Wiremock

The project uses Wiremock to mock external dependencies, particularly the GitHub API.

1. Start the Wiremock server from repository root:

   ```bash
   ./wm/wm.sh
   ```

2. Wiremock runs on port `42069` by default.
3. Mock responses are defined in `wm/mappings`.

## Additional Development Information

### Project Structure

- `src/main/kotlin/de/noah_ruben/` - application code
- `src/main/resources/` - runtime resources and config
- `src/test/kotlin/de/noah_ruben/` - test code
- `tailwind/` - Tailwind source and Amper plugin implementation
- `wm/` - Wiremock scripts and mappings
- `docker/` - compose/Tilt orchestration files

### Code Style

Formatting and linting are IDE-first:

- Use IntelliJ/IDEA formatting and inspections.
- Keep consistency with `.editorconfig` and existing code patterns.

### CSS and Static Resources

Tailwind CSS is used for styling.

- Edit source styles in `tailwind/style.css`.
- Generated CSS is contributed as build resources by the Tailwind Amper plugin.
- Do not commit or manually edit generated CSS artifacts under `src/main/resources/static/`.
