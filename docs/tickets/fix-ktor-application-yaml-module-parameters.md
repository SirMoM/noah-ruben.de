---
status: todo
created: 2026-02-25
completed:
---

# Fix: Ktor application.yaml "Module can't have parameters"

## Goal

The Ktor IDE inspection reports "Module can't have parameters" for the module entry in
`src/main/resources/application.yaml` at line 11. The module function `de.noah_ruben.ApplicationKt.module`
is configured with a default parameter (`repositoryClient`), which Ktor's YAML-based module loading
does not support — modules loaded via `application.yaml` must be no-arg functions.

The `module` function currently has the signature:
```kotlin
fun Application.module(repositoryClient: RepositoryClient = WiremockClient(url = getGithubURL()))
```

This needs to be refactored so the YAML-invoked entry point is a no-arg function, while tests can
still inject a fake client.

## Acceptance criteria

- [ ] `Application.module()` (or a dedicated no-arg entry-point function) takes no parameters and is
      resolvable from `application.yaml`.
- [ ] Tests continue to inject `GithubClientFake` / `WiremockClient` without relying on the parameter
      default — use test helpers or a separate `testModule` function if needed.
- [ ] Ktor IDE inspection no longer reports "Module can't have parameters" for `application.yaml`.
- [ ] `./amper build` passes.
- [ ] `./amper test` passes.

## Notes

- Inspection source: `issues/index.html` — Ktor group → "Ktor application.yaml" inspection, WARNING.
- Current `application.yaml` line 11: `- de.noah_ruben.ApplicationKt.module`
- The existing test helper `testApplicationWithRepositoryFake` in `src/test/` already sets up the
  cache with a fake; verify it does not rely on calling `module(fakeClient)` directly before changing
  the signature.
