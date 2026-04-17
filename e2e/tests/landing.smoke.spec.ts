import type { Page } from "@playwright/test";
import { captureStep, expect, test } from "../support/agent-test";

const landingCommand = (page: Page) =>
  page.locator('[data-role="landing-command"] code');
const landingProfileImage = (page: Page) =>
  page.locator('[data-role="landing-profile-image"]');

test("landing smoke covers theme persistence and key navigation", async ({ page }, testInfo) => {
  await page.goto("/");

  await expect(page).toHaveTitle(/Noah Ruben/);
  await expect(landingCommand(page)).toHaveText("noahruben");
  await expect(landingProfileImage(page)).toHaveAttribute(
    "src",
    "/resources/images/noah-ruben-profile.jpg",
  );
  await page.waitForFunction(
    () => Boolean((window as typeof window & { __noahrubenLandingEffectsTest?: unknown }).__noahrubenLandingEffectsTest),
  );
  await page.evaluate(() => {
    (
      window as typeof window & {
        __noahrubenLandingEffectsTest: { nextEffectName: string | null };
      }
    ).__noahrubenLandingEffectsTest.nextEffectName = "ascii";
  });
  await landingProfileImage(page).click();
  await expect(landingProfileImage(page)).toHaveAttribute("src", /data:image\/png;base64,/);
  await expect(landingProfileImage(page)).toHaveAttribute("alt", /ascii effect applied/);
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

test("cli emulation keeps pending text until a command is submitted", async ({ page }, testInfo) => {
  await page.goto("/");

  const input = page.locator('#cle input[name="command"]');

  await expect(input).toBeVisible();
  await input.click();
  await input.fill("noahruben projects");
  await page.waitForTimeout(1000);
  await expect(input).toHaveValue("noahruben projects");

  await captureStep(page, testInfo, "landing-cli-pending-text");
});
