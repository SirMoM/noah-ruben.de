# Website Auto-Restart on Image Rebuild Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Make the `website` Tilt resource automatically restart whenever the `website-image-build` local resource completes a new image.

**Architecture:** Remove `auto_init=False` from the `website` dc_resource and set `trigger_mode=TRIGGER_MODE_AUTO`. Tilt will then manage the container lifecycle and automatically restart it when its `resource_deps` finish.

**Tech Stack:** Tilt, Docker Compose

---

### Task 1: Update `docker/Tiltfile`

**Files:**
- Modify: `docker/Tiltfile:14-19`

**Step 1: Apply the change**

In `docker/Tiltfile`, replace the `dc_resource('website', ...)` block:

```python
# Before
dc_resource(
    'website',
    auto_init=False,
    resource_deps=['website-image-build', 'wiremock-website'],
    labels=['website'],
)

# After
dc_resource(
    'website',
    trigger_mode=TRIGGER_MODE_AUTO,
    resource_deps=['website-image-build', 'wiremock-website'],
    labels=['website'],
)
```

**Step 2: Verify in Tilt UI**

Restart Tilt (`tilt up` in `docker/`) and confirm:
- `website-image-build` triggers automatically on source changes.
- `website` container starts automatically after `website-image-build` and `wiremock-website` are healthy.
- Changing a source file causes `website-image-build` to re-run, followed by an automatic restart of the `website` container.

**Step 3: Commit**

```bash
git add docker/Tiltfile
git commit -m "fix: auto-restart website container when image is rebuilt"
```
