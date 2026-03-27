import type { Page } from "@playwright/test";
import { expect, test } from "../support/agent-test";

const debugPanel = (page: Page) =>
  page.locator("#noahruben-debug-panel");

const commandInput = (page: Page) =>
  page.locator('#cle input[name="command"]');

const waitForCommandResponse = (page: Page) =>
  page.waitForResponse((response) => {
    const request = response.request();
    return request.method() === "POST" && new URL(response.url()).pathname === "/command";
  });

test("debug helper shows runtime diagnostics and survives cli swaps", async ({ page }) => {
  await page.goto("/?debug=1");

  const panel = debugPanel(page);
  const input = commandInput(page);

  await expect(panel).toBeVisible();
  await expect.poll(async () => await panel.getAttribute("data-health-status")).toMatch(/ok|degraded/);
  await expect(panel).toContainText("path    /?debug=1");

  await expect(input).toBeVisible();
  await input.click();
  await input.clear();
  await input.pressSequentially("noahruben projects");

  const response = waitForCommandResponse(page);
  await input.press("Enter");
  await response;

  await expect(page.locator("h1", { hasText: "Projects" })).toBeVisible();
  await expect(panel).toBeVisible();
  await expect.poll(async () => await panel.getAttribute("data-last-command")).toBe("noahruben projects");
  await expect.poll(async () => await panel.getAttribute("data-last-htmx")).toContain("/command");
});
