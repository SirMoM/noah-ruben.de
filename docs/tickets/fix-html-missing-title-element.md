---
status: todo
created: 2026-02-25
completed:
---

# Add title element to static index.html

## Goal

`src/main/resources/static/index.html` is missing the required `<title>` element inside `<head>`,
which violates HTML accessibility requirements. Add an appropriate `<title>`.

## Acceptance criteria

- [ ] `<head>` in `src/main/resources/static/index.html` contains a `<title>` element
- [ ] No "Missing required 'title' element" warning in the HTML accessibility inspection

## Notes

Inspection: **HTML > Accessibility > Missing required 'title' element**
Location: `src/main/resources/static/index.html` line 2
