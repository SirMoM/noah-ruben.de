# Tailwind CSS

Tailwind v4 configuration lives in `tailwind/style.css`.

## Build integration

Tailwind generation is integrated into the repository's Amper build through the local `tailwind` plugin:

- Plugin module: `tailwind/module.yaml`
- Task wiring: `tailwind/plugin.yaml`
- Task implementation: `tailwind/src/GenerateTailwind.kt`

The plugin runs `npm ci` and Tailwind CLI, then publishes generated output as JVM resources under `static/style.css`.

Important:
- Do not manually generate or commit `src/main/resources/static/style.css`.
- There is no `tailwind/run.sh` workflow anymore.

## Typical commands

Run from repository root:

```bash
./amper build
./amper test
./amper package -f executable-jar
```

These commands trigger Tailwind generation through Amper when needed.

## Catppuccin note

The Catppuccin Tailwind plugin is still not fully v4-ready.

- Workaround reference: <https://github.com/catppuccin/tailwindcss/issues/19#issuecomment-2494971455>
- Upstream v4 support PR: <https://github.com/catppuccin/tailwindcss/pull/22>
