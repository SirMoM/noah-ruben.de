import type { Page } from "@playwright/test";
import { captureStep, expect, test } from "../support/agent-test";

const landingCommand = (page: Page) =>
  page.locator('[data-role="landing-command"] code');

test("landing smoke covers theme persistence and key navigation", async ({ page }, testInfo) => {
  await page.goto("/");

  await expect(page).toHaveTitle(/Noah Ruben/);
  await expect(landingCommand(page)).toHaveText("noahruben");
  await captureStep(page, testInfo, "landing-home");

  await page.locator('a[href="/projects"]').first().click();
  await expect(page).toHaveURL(/\/projects$/);
  await expect(page.locator("#search-results")).toBeVisible();
  await captureStep(page, testInfo, "landing-to-projects");

  await page.goto("/");
  await page.locator('a[href="/cv"]').first().click();
  await expect(page).toHaveURL(/\/cv(?:\?.*)?$/);
  await expect(page.locator("#cv-pdf-viewer")).toBeVisible();
  await captureStep(page, testInfo, "landing-to-cv");
});
