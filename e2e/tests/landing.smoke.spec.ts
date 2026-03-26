import { captureStep, expect, test } from "../support/agent-test";

test("landing smoke covers theme persistence and key navigation", async ({ page }, testInfo) => {
  await page.goto("/");

  await expect(page).toHaveTitle(/Noah Ruben/);
  await expect(page.getByText(">> noahruben").first()).toBeVisible();
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
