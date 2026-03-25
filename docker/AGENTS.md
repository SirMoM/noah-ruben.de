# AGENTS Guide for `docker/`

This guide is for agentic coding assistants operating in
`/Users/i13az81/dev/uni/noah-ruben.de/docker`.
Keep all commands and file paths rooted in this folder unless a user explicitly says otherwise.

## 1) Scope and Safety

- Primary scope: Docker orchestration and local integration setup in `docker/`.
- Do not assume access to parent-folder source code unless the user explicitly requests it.
- Do not move, rename, or delete `compose.yaml` services without user instruction.
- Never start or stop Docker services yourself (`docker compose up/down/start/stop/restart`, `docker run`, `docker start`) unless the user explicitly requests that action in the current task.
- Treat environment files as sensitive; do not print secrets.
- Prefer minimal, reversible changes.

## 2) Repository Snapshot

Current files in this folder:

- `compose.yaml`
- `wm.env`
- `Tiltfile`

`compose.yaml` defines:

- `website` service using image `website:latest` (no compose-side build)
- `website` service behind profile `website`
- `website` depends on healthy `wiremock`
- host port mapping `42081:42081`
- runtime env file `${ENV_FILE:-.env}`
- `wiremock` service (no profile gate) with healthcheck
- wiremock mapping volume `../wm:/home/wiremock`
- wiremock port mapping `8080:8080`

`Tiltfile` defines:

- local image build resource: `website-image-build`
- manual runtime resource: `website`
- auto-managed mock resource: `wiremock`
- auto-reload helper: `wiremock-reload`

## Docker Stack

- Inspect containers: `docker ps --format json | jq .`

## 6) Configuration Notes

- `website` reads env file from `${ENV_FILE:-.env}`.
- `website` now sets `CV_PATH=/cv` inside the container.
- Compose mount sources can be overridden with `CV_ENG_DIR` and `CV_GER_DIR`.
- Default host mount sources are `/Users/i13az81/dev/uni/pers/cv/out/eng` and `/Users/i13az81/dev/uni/pers/cv/out/ger`.
- For direct Docker Compose usage with custom env file, users can start manually with:
  - `ENV_FILE=wm.env docker compose -f compose.yaml up`
- Agents should not execute startup commands unless explicitly asked.
- Keep env values out of logs and commit history.
- If adding env keys, document expected defaults in this file.
