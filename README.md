# noah-ruben.de

## Development mode

1. Start the Wiremock server:
   - Use an IDE run configuration, or
   - run `./wm/wm.sh`

2. Build the project with Amper:

   ```bash
   ./amper build
   ```

   Note: Tailwind CSS generation is part of the Amper build via the local `tailwind` plugin. No separate `tailwind/run.sh` step is required.

3. Run tests with Amper:

   ```bash
   ./amper test
   ```

4. Run a single test class or test method:

   ```bash
   ./amper test --include-classes="de.noah_ruben.site.LandingPageTest"
   ./amper test --include-test="de.noah_ruben.site.LandingPageTest.landingPage"
   ```

5. Run the application:

   ```bash
   GITHUB_TOKEN="NOT_NEEDED" GITHUB_URL="http://localhost:42069" ./amper run
   ```

6. Build the deployable artifact:

   ```bash
   ./amper package -f executable-jar
   ```

## Tilt (Docker orchestration)

Run from `docker/`:

1. Start Tilt:

   ```bash
   tilt up
   ```

2. Start the website runtime/deploy step (separate from image build):

   ```bash
   tilt trigger website
   ```

3. Stop Tilt-managed resources:

   ```bash
   tilt down
   ```

Expected behavior:
- `website-image-build` is the image build job.
- `website` is the runtime/deploy job (manual trigger) and depends on `website-image-build`.
- `docker/compose.yaml` uses `image: website:latest` (no compose-side build).
- Default run manages `wiremock`; `website` is only started when `website` is triggered.
- `wiremock-reload` watches `wm/` mappings and restarts Wiremock automatically on changes.
- Logs and resource status are visible in Tilt UI/CLI.
