---
status: done
created: 2026-02-25
completed: 2026-02-25
tickets: []
---

# Tilt Website Auto-Reload Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Make Tilt reload the `website` service automatically after `website` image rebuild while keeping manual initial startup.

**Architecture:** Update the existing `dc_resource('website')` in the Tiltfile to rely on default automatic trigger behavior and preserve dependency-driven updates from `website-image-build`. Keep `auto_init=False` so startup remains manual.

**Tech Stack:** Tiltfile (Starlark), Docker Compose integration

---

### Task 1: Update website trigger behavior in Tilt

**Files:**
- Modify: `docker/Tiltfile`
- Test: manual Tilt validation in local environment

**Step 1: Define expected behavior check (configuration change exception to TDD)**

Expected behavior:
- `website` does not auto-start on `tilt up`.
- After first manual start, image rebuild causes automatic website reload.

**Step 2: Apply minimal implementation**

Edit `dc_resource('website')` in `docker/Tiltfile`:
- remove `trigger_mode=TRIGGER_MODE_MANUAL`
- keep `auto_init=False`
- keep existing `resource_deps`

Resulting block:

```python
dc_resource(
    'website',
    auto_init=False,
    resource_deps=['website-image-build', 'wiremock-website'],
    labels=['website'],
)
```

**Step 3: Verify configuration is syntactically correct**

Run: `git diff -- docker/Tiltfile`
Expected: only the manual trigger line is removed for `website` resource.

**Step 4: Verify runtime behavior in Tilt (manual check)**

Run in local Tilt UI/session:
1. Start Tilt.
2. Trigger `website` once manually.
3. Touch/modify a dependency path of `website-image-build`.
4. Confirm `website-image-build` runs, then `website` reloads automatically.

Expected: website update occurs without another manual trigger.

**Step 5: Commit**

```bash
git add docker/Tiltfile
git commit -m "chore: auto-reload website after image rebuild in Tilt"
```
