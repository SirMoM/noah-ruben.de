/**
 * @typedef {object} HealthData
 * @property {string} bootId
 * @property {number} debugHealthPollIntervalMs
 * @property {string} startupTime
 * @property {string} overallStatus
 * @property {string} version
 */

(() => {
    const DEFAULT_HEALTH_POLL_INTERVAL_MS = 1500;
    const FLASH_CLASS = "noahruben-debug-flash";
    const FLASH_STYLE_ID = "noahruben-debug-flash-style";
    const MIN_HEALTH_POLL_INTERVAL_MS = 100;
    const PANEL_ID = "noahruben-debug-panel";
    const debugParam = new URLSearchParams(window.location.search).get("debug");
    const enabled = shouldEnable(debugParam);
    const flashEnabled = window.__noahrubenDebugConfig?.flashEnabled !== false;

    if (!enabled) {
        return;
    }

    /** @type {HTMLPreElement | null} */
    let panel = null;
    let visible = debugParam === "1" || debugParam === "true" || debugParam === "on";
    let healthPollIntervalMs = DEFAULT_HEALTH_POLL_INTERVAL_MS;
    let healthPollTimeout = null;
    let healthRequestInFlight = false;
    let initialBootId = null;
    let reloadInProgress = false;

    const state = {
        bootId: "-",
        health: "idle",
        lastCommand: "-",
        lastHtmx: "-",
        startupTime: "-",
        version: "-",
    };

    function shouldEnable(param) {
        if (param === "0" || param === "false" || param === "off") {
            return false;
        }

        return isLocalHost() || param === "1" || param === "true" || param === "on";
    }

    function isLocalHost() {
        const { hostname } = window.location;
        return hostname === "localhost" || hostname === "127.0.0.1" || hostname === "::1";
    }

    function currentTheme() {
        if (document.documentElement.classList.contains("mocha")) {
            return "dark (mocha)";
        }

        if (document.documentElement.classList.contains("latte")) {
            return "light (latte)";
        }

        return "unknown";
    }

    function currentView() {
        const heading = document.querySelector("h1")?.textContent?.trim();
        if (heading) {
            return heading;
        }

        return document.title.trim() || "unknown";
    }

    function normalizeHealthPollInterval(value) {
        const interval = Number(value);
        if (!Number.isFinite(interval) || interval < MIN_HEALTH_POLL_INTERVAL_MS) {
            return DEFAULT_HEALTH_POLL_INTERVAL_MS;
        }

        return Math.round(interval);
    }

    function scheduleHealthRefresh(delay = healthPollIntervalMs) {
        if (reloadInProgress) {
            return;
        }

        if (healthPollTimeout !== null) {
            window.clearTimeout(healthPollTimeout);
        }

        healthPollTimeout = window.setTimeout(() => {
            void refreshHealth();
        }, Math.max(0, delay));
    }

    function ensureFlashStyle() {
        if (document.getElementById(FLASH_STYLE_ID)) {
            return;
        }

        const style = document.createElement("style");
        style.id = FLASH_STYLE_ID;
        style.textContent = `
            @keyframes noahrubenDebugFlash {
                from { background-color: rgba(166, 227, 161, 0.45); }
                to { background-color: transparent; }
            }

            .${FLASH_CLASS} {
                animation: noahrubenDebugFlash 1500ms ease-out;
            }
        `.trim();
        document.head.append(style);
    }

    function flashSwapTarget(target) {
        if (!flashEnabled) {
            return;
        }

        if (!(target instanceof HTMLElement)) {
            return;
        }

        ensureFlashStyle();
        target.classList.remove(FLASH_CLASS);
        void target.offsetWidth;
        target.classList.add(FLASH_CLASS);
        target.addEventListener("animationend", () => {
            target.classList.remove(FLASH_CLASS);
        }, { once: true });
    }

    function ensurePanel() {
        if (panel?.isConnected) {
            return panel;
        }

        panel = document.createElement("pre");
        panel.id = PANEL_ID;
        panel.style.position = "fixed";
        panel.style.right = "1rem";
        panel.style.bottom = "1rem";
        panel.style.zIndex = "2147483647";
        panel.style.maxWidth = "min(26rem, calc(100vw - 1rem))";
        panel.style.margin = "0";
        panel.style.padding = "0.9rem 1rem";
        panel.style.borderRadius = "0.85rem";
        panel.style.boxShadow = "0 1rem 2rem rgba(0, 0, 0, 0.25)";
        panel.style.font = '12px/1.45 "Cascadia Code", monospace';
        panel.style.whiteSpace = "pre-wrap";
        panel.style.pointerEvents = "none";
        panel.style.backdropFilter = "blur(10px)";
        document.body.append(panel);

        return panel;
    }

    function removePanel() {
        if (!panel?.isConnected) {
            panel = null;
            return;
        }

        panel.remove();
        panel = null;
    }

    function applyTheme() {
        if (!panel) {
            return;
        }

        if (currentTheme().startsWith("light")) {
            panel.style.background = "rgba(239, 241, 245, 0.94)";
            panel.style.border = "1px solid rgba(76, 79, 105, 0.15)";
            panel.style.color = "#4c4f69";
            return;
        }

        panel.style.background = "rgba(17, 17, 27, 0.94)";
        panel.style.border = "1px solid rgba(255, 255, 255, 0.14)";
        panel.style.color = "#f5e0dc";
    }

    function render() {
        if (!visible) {
            removePanel();
            return;
        }

        const element = ensurePanel();
        applyTheme();

        element.dataset.healthStatus = state.health;
        element.dataset.lastCommand = state.lastCommand;
        element.dataset.lastHtmx = state.lastHtmx;
        element.textContent = [
            "noahruben debug",
            `path    ${window.location.pathname}${window.location.search}${window.location.hash}`,
            `view    ${currentView()}`,
            `theme   ${currentTheme()}`,
            `health  ${state.health}`,
            `version ${state.version}`,
            `boot    ${state.bootId}`,
            `startup ${state.startupTime}`,
            `command ${state.lastCommand}`,
            `htmx    ${state.lastHtmx}`,
            "",
            "toggle  Ctrl/Cmd+Shift+D",
        ].join("\n");
    }

    async function refreshHealth() {
        if (healthRequestInFlight || reloadInProgress) {
            return;
        }

        healthRequestInFlight = true;
        try {
            const response = await fetch("/health", {
                cache: "no-store",
                headers: {
                    Accept: "application/json",
                },
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            /** @type {HealthData} */
            const data = await response.json();
            const nextBootId = typeof data.bootId === "string" && data.bootId.length > 0
                ? data.bootId
                : null;

            state.health = data.overallStatus || "unknown";
            state.startupTime = data.startupTime || "-";
            state.version = data.version || "-";
            state.bootId = nextBootId || "-";
            healthPollIntervalMs = normalizeHealthPollInterval(data.debugHealthPollIntervalMs);

            if (initialBootId !== null && nextBootId !== null && nextBootId !== initialBootId) {
                reloadInProgress = true;
                window.location.reload();
                return;
            }

            if (initialBootId === null && nextBootId !== null) {
                initialBootId = nextBootId;
            }
        } catch {
            state.health = "unreachable";
            state.startupTime = "-";
        } finally {
            healthRequestInFlight = false;
        }

        render();
        scheduleHealthRefresh();
    }

    function toggle() {
        visible = !visible;
        render();

        if (visible) {
            void refreshHealth();
        }
    }

    function bindEvents() {
        document.addEventListener("keydown", (event) => {
            const isShortcut = (event.ctrlKey || event.metaKey)
                && event.shiftKey
                && event.key.toLowerCase() === "d";

            if (!isShortcut) {
                return;
            }

            event.preventDefault();
            toggle();
        });

        document.addEventListener("htmx:beforeRequest", (event) => {
            const detail = event.detail ?? {};
            const method = detail.requestConfig?.verb?.toUpperCase() || "GET";
            const path = detail.pathInfo?.requestPath || "unknown";
            const command = detail.parameters?.command;

            if (typeof command === "string" && command.trim().length > 0) {
                state.lastCommand = command.trim();
            }

            state.lastHtmx = `${method} ${path} pending`;
            render();
        });

        document.addEventListener("htmx:afterRequest", (event) => {
            const detail = event.detail ?? {};
            const method = detail.requestConfig?.verb?.toUpperCase() || "GET";
            const path = detail.pathInfo?.requestPath || "unknown";
            const status = detail.xhr?.status ?? "done";

            state.lastHtmx = `${method} ${path} -> ${status}`;
            render();

            if (visible) {
                void refreshHealth();
            }
        });

        document.addEventListener("htmx:afterSwap", (event) => {
            flashSwapTarget(event.detail?.elt ?? event.detail?.target);
            render();
        });

        window.addEventListener("noahruben:theme-changed", () => {
            render();
        });

        window.addEventListener("popstate", () => {
            render();
        });
    }

    function init() {
        bindEvents();
        render();
        scheduleHealthRefresh(0);
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init, { once: true });
    } else {
        init();
    }
})();
