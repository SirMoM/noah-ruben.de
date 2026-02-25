---
status: todo
created: 2026-02-25
completed:
---

# Rename snake_case properties in Repository data class to camelCase

## Goal

`src/main/kotlin/de/noah_ruben/data/model/github/Repository.kt` is a data class that mirrors
the GitHub API JSON response. Its 21 properties use `snake_case` (e.g. `node_id`, `full_name`,
`html_url`, `forks_count`, etc.), which violates Kotlin property naming conventions.

Use `@SerialName` (or equivalent JSON annotation) to map the JSON snake_case keys to camelCase
Kotlin property names so the class is both API-compatible and convention-compliant.

## Acceptance criteria

- [ ] All 21 underscore-named properties renamed to camelCase equivalents
- [ ] JSON deserialization still works correctly (mapping preserved via annotation)
- [ ] No "Property name should not contain underscores" weak warnings remain in `Repository.kt`
- [ ] All tests pass

## Notes

Inspection: **Kotlin > Naming conventions > Property naming convention**
Location: `src/main/kotlin/de/noah_ruben/data/model/github/Repository.kt` — 21 properties
Affected names: `node_id`, `full_name`, `html_url`, `forks_count`, `stargazers_count`,
`watchers_count`, `default_branch`, `open_issues_count`, `is_template`, `has_issues`,
`has_projects`, `has_wiki`, `has_pages`, `has_downloads`, `has_discussions`, `pushed_at`,
`created_at`, `updated_at`, `open_issues`, `allow_forking`, `web_commit_signoff_required`

Any existing references to these properties in the codebase will need updating.
