---
status: done
created: 2026-02-25
completed: 2026-02-25
tickets: []
---

# Tilt Website Auto-Reload Design

## Problem

`website-image-build` rebuilds the Docker image automatically, but the `website` service in Tilt is configured with manual trigger mode. This means the website container does not reload after image rebuild unless triggered manually.

## Goal

Keep manual initial startup behavior while enabling automatic reload after image rebuild.

## Selected Approach

Use a Tilt-native dependency-driven reload flow:

- Keep `auto_init=False` on `dc_resource('website')` so the service is not started automatically on `tilt up`.
- Remove `trigger_mode=TRIGGER_MODE_MANUAL` from `dc_resource('website')` so it falls back to automatic trigger behavior.
- Preserve `resource_deps=['website-image-build', 'wiremock-website']` so updates to the built image trigger website redeploy.

## Why This Approach

- Minimal change in one file.
- No extra helper resources or shell restart commands.
- Aligns with Tilt's resource dependency model.
- Matches desired UX: manual first start, automatic subsequent reloads after rebuild.

## Risks and Mitigations

- Risk: behavior depends on Tilt's default trigger semantics for `dc_resource`.
- Mitigation: verify in local Tilt by triggering one rebuild and observing automatic website update.

## Validation

- Start Tilt.
- Manually trigger `website` once.
- Modify a file under `../src` (or another `website-image-build` dependency).
- Confirm `website-image-build` runs and `website` reloads without manual trigger.
