---
status: todo
created: 2026-02-25
completed:
---

# Resolve unresolvable wiki-link placeholder in plan.md template

## Goal

`docs/templates/plan.md` line 6 contains `[[ticket-slug]]`, a placeholder wiki link that
the IDE cannot resolve. Either replace the placeholder with a real or non-link example, or
suppress the inspection for the template file so it does not produce noise.

## Acceptance criteria

- [ ] No "Cannot resolve wiki link: [[ticket-slug]]" errors in `docs/templates/plan.md`

## Notes

Inspection: **General > Annotator**
Location: `docs/templates/plan.md` line 6 (4 identical errors for the same link)
Simplest fix: replace `[[ticket-slug]]` with a comment or plain-text example like `<ticket-slug>`.
