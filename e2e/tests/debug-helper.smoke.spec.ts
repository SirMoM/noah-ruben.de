import type { Page } from "@playwright/test";
import { expect, test } from "../support/agent-test";

const FLASH_ANIMATION = "noahrubenDebugFlash";
const FLASH_CLASS = "noahruben-debug-flash";

const debugPanel = (page: Page) =>
  page.locator("#noahruben-debug-panel");

const debugState = (page: Page) =>
  page.locator("#search-replace");

const commandInput = (page: Page) =>
  page.locator('#cle input[name="command"]');

const waitForCommandResponse = (page: Page) =>
  page.waitForResponse((response) => {
    const request = response.request();
    return request.method() === "POST" && new URL(response.url()).pathname === "/command";
  });

const waitForSearchResponse = (page: Page) =>
  page.waitForResponse((response) => {
    const request = response.request();
    return request.method() === "POST" && new URL(response.url()).pathname === "/search";
  });

type FlashRecord = {
  animationName: string;
  backgroundColor: string;
  hasFlashClass: boolean;
  phase: string;
  targetId: string;
};

const installFlashRecorder = async (page: Page): Promise<void> => {
  await page.evaluate(({ animationName, className }) => {
    type DebugWindow = Window & typeof globalThis & {
      __noahrubenDebugFlashEvents?: FlashRecord[];
      __noahrubenDebugFlashRecorderInstalled?: boolean;
    };

    const debugWindow = window as DebugWindow;
    if (debugWindow.__noahrubenDebugFlashRecorderInstalled) {
      return;
    }

    debugWindow.__noahrubenDebugFlashRecorderInstalled = true;
    debugWindow.__noahrubenDebugFlashEvents = [];

    const record = (phase: string, event: Event) => {
      if (!(event instanceof AnimationEvent) || event.animationName !== animationName) {
        return;
      }

      const target = event.target;
      if (!(target instanceof HTMLElement)) {
        return;
      }

      debugWindow.__noahrubenDebugFlashEvents?.push({
        animationName: event.animationName,
        backgroundColor: window.getComputedStyle(target).backgroundColor,
        hasFlashClass: target.classList.contains(className),
        phase,
        targetId: target.id,
      });
    };

    document.addEventListener("animationstart", (event) => {
      record("start", event);
    }, true);
    document.addEventListener("animationend", (event) => {
      record("end", event);
    }, true);
  }, { animationName: FLASH_ANIMATION, className: FLASH_CLASS });
};

const clearFlashEvents = async (page: Page): Promise<void> => {
  await page.evaluate(() => {
    type DebugWindow = Window & typeof globalThis & {
      __noahrubenDebugFlashEvents?: FlashRecord[];
    };

    const debugWindow = window as DebugWindow;
    debugWindow.__noahrubenDebugFlashEvents = [];
  });
};

const expectFlashVisible = async (page: Page, targetId: string): Promise<void> => {
  await expect.poll(async () => await page.evaluate(({ animationName, targetId: expectedTargetId }) => {
    type DebugWindow = Window & typeof globalThis & {
      __noahrubenDebugFlashEvents?: FlashRecord[];
    };

    const debugWindow = window as DebugWindow;
    return debugWindow.__noahrubenDebugFlashEvents?.some((event) =>
      event.phase === "start"
      && event.targetId === expectedTargetId
      && event.animationName === animationName
      && event.hasFlashClass
      && event.backgroundColor !== "rgba(0, 0, 0, 0)"
      && event.backgroundColor !== "transparent"
    ) ?? false;
  }, { animationName: FLASH_ANIMATION, targetId })).toBe(true);
};

const expectFlashCleared = async (page: Page, selector: string): Promise<void> => {
  await page.waitForFunction(
    ({ className, selector: targetSelector }) => {
      const target = document.querySelector(targetSelector);
      return target instanceof HTMLElement && !target.classList.contains(className);
    },
    { className: FLASH_CLASS, selector },
  );
};

test("debug helper shows runtime diagnostics and visible flash for projects and cli swaps", async ({ page }) => {
  await page.goto("/projects?debug=1");
  await installFlashRecorder(page);

  const panel = debugPanel(page);
  const projectsShell = debugState(page);
  const input = commandInput(page);
  const queryInput = page.getByLabel("Query");
  const resultsSummary = page.locator("#projects-results-summary");

  await expect(panel).toBeVisible();
  await expect.poll(async () => await panel.getAttribute("data-health-status")).toMatch(/ok|degraded/);
  await expect(panel).toContainText("path    /projects?debug=1");
  await expect(projectsShell).toBeVisible();

  await clearFlashEvents(page);
  const searchResponse = waitForSearchResponse(page);
  await queryInput.fill("__definitely_no_matches__");
  await searchResponse;

  await expectFlashVisible(page, "search-replace");
  await expectFlashCleared(page, "#search-replace");
  await expect(resultsSummary).toHaveText("0 results");
  await expect.poll(async () => await panel.getAttribute("data-last-htmx")).toContain("/search");

  await expect(input).toBeVisible();
  await input.click();
  await input.clear();
  await input.pressSequentially("noahruben");

  await clearFlashEvents(page);
  const response = waitForCommandResponse(page);
  await input.press("Enter");
  await response;

  await expectFlashVisible(page, "body");
  await expectFlashCleared(page, "#body");
  await expect(page.getByText("System summary")).toBeVisible();
  await expect(panel).toBeVisible();
  await expect.poll(async () => await panel.getAttribute("data-last-htmx")).toContain("/command");
});

test("debug helper reloads the page when the backend boot id changes", async ({ page }) => {
  let healthRequestCount = 0;

  await page.addInitScript(() => {
    const key = "__noahrubenLoadCount";
    const current = Number(sessionStorage.getItem(key) ?? "0");
    sessionStorage.setItem(key, String(current + 1));
  });

  await page.route("**/health", async (route) => {
    healthRequestCount += 1;
    const bootId = healthRequestCount < 2
      ? "11111111-1111-1111-1111-111111111111"
      : "22222222-2222-2222-2222-222222222222";

    await route.fulfill({
      body: JSON.stringify({
        bootId,
        checks: {
          application: {
            message: "Application is running.",
            status: "ok",
          },
        },
        debugHealthPollIntervalMs: 50,
        overallStatus: "ok",
        startupTime: "2026-04-14T12:00:00",
        version: "test",
      }),
      contentType: "application/json",
      status: 200,
    });
  });

  await page.goto("/projects");

  await expect.poll(async () => Number(await page.evaluate(
    () => sessionStorage.getItem("__noahrubenLoadCount") ?? "0",
  ))).toBe(2);
});
