# Observability Compose Profile Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add an optional local Elastic/EDOT observability stack that is only enabled through Tilt config and does not force OTEL exporters on for the default website runtime.

**Architecture:** Keep the Java agent in the website image, but gate exporter activation and the supporting Elasticsearch/Kibana/EDOT services behind a compose profile. Use `docker/tilt_config.json` plus Tiltfile env injection to switch the profile and OTEL exporter env vars on together.

**Tech Stack:** Tilt, Docker Compose profiles, Elasticsearch, Kibana, Elastic Agent in EDOT mode, Ktor OpenTelemetry instrumentation

---

## Chunk 1: Rebase And Keep Only Relevant OTEL App Changes

### Task 1: Rebase the worktree onto `dud`

**Files:**
- Modify: git worktree metadata only

- [ ] Stash the existing OTEL worktree changes
- [ ] Switch the worktree to `dud`
- [ ] Repoint `codex-otel-observability` to `dud`
- [ ] Restore the stash and resolve conflicts

### Task 2: Drop stale blog-specific observability changes

**Files:**
- Modify: `module.yaml`
- Delete: `src/main/kotlin/de/noah_ruben/data/blog/BlogIngestionService.kt`
- Delete: `src/main/kotlin/de/noah_ruben/data/blog/BlogIngestionTelemetry.kt`
- Delete: `src/test/kotlin/de/noah_ruben/data/blog/BlogIngestionServiceTest.kt`

- [ ] Keep `ktor-server-call-id-jvm` and `opentelemetry-api`
- [ ] Remove stale blog-only dependencies and files that no longer exist on `dud`

## Chunk 2: Add The Optional Elastic/EDOT Stack

### Task 3: Add compose services and collector config

**Files:**
- Modify: `docker/compose.yaml`
- Create: `docker/edot-collector-config.yml`

- [ ] Add `elasticsearch`, `kibana`, and `edot-collector` behind a new `observability` compose profile
- [ ] Keep the website service always present
- [ ] Default OTEL exporters to `none` on the website unless Tilt enables the stack
- [ ] Add the minimal EDOT pipelines for OTLP logs, metrics, traces, and aggregated APM metrics

### Task 4: Add the Tilt toggle

**Files:**
- Modify: `docker/Tiltfile`
- Create: `docker/tilt_config.json`

- [ ] Parse `enable_observability_stack` from `docker/tilt_config.json`
- [ ] Expand `COMPOSE_PROFILES` to include `observability` when enabled
- [ ] Inject the OTEL exporter env vars only when the observability stack is enabled
- [ ] Register Elastic resources in Tilt only when the profile is active

## Chunk 3: Update Docs

### Task 5: Document the local toggle and stack shape

**Files:**
- Modify: `README.md`
- Modify: `docker/AGENTS.md`

- [ ] Document `docker/tilt_config.json`
- [ ] Document the new optional services and their ports
- [ ] Keep it clear that runtime verification is still deferred
