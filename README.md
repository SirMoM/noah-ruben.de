# noah-ruben.de

## Development mode

1. Start the Wiremock server:
   - Use an IDE run configuration, or
   - run `./wm/wm.sh`

2. Build the project with Amper:

   ```bash
   ./amper build
   ```

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
   ./amper package
   ```

7. If CSS changes, run Tailwind explicitly before build/test/package:

   ```bash
   ./tailwind/run.sh
   ```
