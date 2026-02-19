# Gradle to Amper Migration Checklist

## Acceptance Criteria

- [x] `./amper test` executes the repository test suite.
- [x] Single-test execution strategy is documented for Amper CLI.
- [x] CI uses Amper commands only (`./amper build`, `./amper test`).
- [x] Lint/format workflow is explicitly IDE-first in docs.
- [x] Dockerfile builds a runnable JAR using `./amper package` in Linux build stage.
- [x] `./amper build` succeeds locally.
- [x] `docker build -t noah-ruben:amper .` succeeds locally.

Note: Per migration execution constraints, `./amper run` is intentionally not used in this migration run.

## Baseline (pre-migration) Observations

### Baseline command: `gradle test -x runTailwind -x lintKotlin`

Result: **FAILED** during Gradle task configuration.

Notable output:
- `Could not create task ':test'`
- `Could not create task of type 'Test'`
- `Type T not present`

### Baseline command: `GITHUB_URL="http://localhost:42069" gradle run`

Result: **FAILED** in `runTailwind` before application startup.

Notable output:
- `cp: ../build/resources/main/static: No such file or directory`
- `Execution failed for task ':runTailwind'`

Conclusion: pre-migration baseline is currently not green; migration validation will be based on Amper command success and documented behavior deltas.

## Amper Validation

### Config introspection

- `./amper show modules` -> module `noah-ruben.de` detected.
- `./amper show dependencies` -> dependencies resolved (Ktor/JVM dependency graph produced).

### Build and test parity checks

- `./amper build` -> **PASSED**.
- `./amper test` -> **PASSED** (all tests green, including `CacheTest`).
- `./amper test --include-test="de.noah_ruben.site.LandingPageTest.landingPage"` -> **PASSED**.

### Single-test strategy (Amper)

Confirmed via `./amper test --help`:
- Single class: `./amper test --include-classes="de.noah_ruben.site.LandingPageTest"`
- Single method: `./amper test --include-test="de.noah_ruben.site.LandingPageTest.landingPage"`
