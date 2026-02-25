---
status: todo
created: 2026-02-25
completed:
---

# Add lang attribute to static index.html

## Goal

`src/main/resources/static/index.html` is missing the required `lang` attribute on the `<html>`
element, which is an accessibility requirement. Add `lang="en"` (or the appropriate language code).

## Acceptance criteria

- [ ] `<html>` element in `src/main/resources/static/index.html` has a `lang` attribute
- [ ] No "Missing required 'lang' attribute" warning in the HTML accessibility inspection

## Notes

Inspection: **HTML > Accessibility > Missing required 'lang' attribute**
Location: `src/main/resources/static/index.html` line 1
