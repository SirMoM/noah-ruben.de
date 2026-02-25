---
status: todo
created: 2026-02-25
completed:
---

# Clean up redundant entries in .dockerignore

## Goal

`.dockerignore` contains two redundant entries:

- `*.iml` at line 15 is already covered by `.idea` at line 13
- `node_modules` at line 18 is already covered by `tailwind/node_modules` at line 77

Remove the redundant entries to keep the file minimal and avoid confusion.

## Acceptance criteria

- [ ] `*.iml` line removed (covered by `.idea`)
- [ ] `node_modules` line removed (covered by `tailwind/node_modules`)
- [ ] No "Cover entry" warnings remain in `.dockerignore`

## Notes

Inspection: **Ignore > Cover entry**
Location: `.dockerignore` lines 15, 18
