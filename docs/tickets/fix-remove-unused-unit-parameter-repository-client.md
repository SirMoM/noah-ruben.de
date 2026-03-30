---
status: todo
created: 2026-02-25
completed:
---

# Remove unused `unit` parameter from RepositoryClient.getRepositoryLanguages

## Goal

`RepositoryClient.getRepositoryLanguages` in
`src/main/kotlin/de/noah_ruben/data/GithubClient.kt` declares a `unit: Function0<Unit>`
parameter that is not used in any implementation. Remove the parameter from the interface
and all implementations to keep the signature clean.

## Acceptance criteria

- [ ] `unit` parameter removed from `RepositoryClient.getRepositoryLanguages` interface method
- [ ] All implementations (e.g. `GitHubClient`, `WiremockClient`) updated accordingly
- [ ] All call sites updated; the affected data tests still pass
- [ ] The dead callback block currently passed from `Cache` is removed rather than activated as runtime delay behavior
- [ ] No "Parameter 'unit' is not used in any implementation" warning remains

## Notes

Inspection: **Java > Declaration redundancy > Unused declaration**
Location: `src/main/kotlin/de/noah_ruben/data/GithubClient.kt` — `RepositoryClient` interface
`Cache.kt` currently passes a lambda containing `sleep(200)`, but all `getRepositoryLanguages`
implementations ignore the callback today.
This task should remove that dead code instead of introducing a real delay into production flow.
`CacheTest.kt` is the existing test coverage for the affected cache flow and should remain green.
