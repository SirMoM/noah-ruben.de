---
status: todo
created: 2026-02-25
completed:
---

# Fix: Spelling / typos across the project

## Goal

The JetBrains Proofreading → Spelling inspection reports 181 typos spread across the project.
Rather than creating one ticket per typo, this ticket covers a bulk review-and-fix pass using
the IDE's built-in spell checker (IntelliJ "Fix all 'Typo' problems in file") or a tool such
as `typos` / `codespell`.

The goal is to fix genuine misspellings in comments, documentation, string literals, and variable
names while accepting/adding to the project dictionary all domain-specific terms that the spell
checker does not know (e.g. `Catppuccin`, `Frappe`, `htmx`, `HTMX`, `kotlinx`, `ktor`, `Wiremock`).

## Acceptance criteria

- [ ] All clear-cut misspellings in source code comments, Kotlin strings, and Markdown prose are
      corrected.
- [ ] Domain-specific words (`catppuccin`, `frappe`, `htmx`, `kotlinx`, `ktor`, `wiremock`, etc.)
      are added to the project spell-check dictionary (`.idea/dictionaries/` or an `.editorconfig`
      wordlist) so they no longer trigger warnings.
- [ ] No functional source code changes are introduced as a side-effect of this pass.
- [ ] `./amper build` passes after the changes.
- [ ] The total typo count in the inspection report is reduced to near zero on the next run.

## Notes

- Inspection source: `issues/index.html` — Proofreading > Spelling inspection, 181 TYPO entries.
- Approach: open `issues/index.html` in a browser, navigate to the Spelling section, and work
  through each entry; or run IntelliJ's "Fix all Typo problems" per file.
- Alternatively, run `codespell` or `typos` CLI from the repo root for a programmatic pass, then
  review the diff.
- Be conservative with identifier/variable renames — if a name is part of a public API or test
  contract, prefer suppressing the warning rather than renaming.
