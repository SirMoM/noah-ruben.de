import type { Page } from "@playwright/test";
import { captureStep, expect, expectTheme, setTheme, test } from "../support/agent-test";

type ThemePage = {
  name: string;
  url: string;
  ready: (page: Page) => Promise<void>;
};

const pages = [
  {
    name: "landing",
    ready: async (page) => {
      await expect(page.locator('[data-role="landing-command"] code')).toHaveText("noahruben");
    },
    url: "/",
  },
  {
    name: "projects",
    ready: async (page) => {
      await expect(page.locator("#search-results")).toBeVisible();
    },
    url: "/projects",
  },
  {
    name: "cv",
    ready: async (page) => {
      await expect(page.locator("#cv-pdf-viewer")).toBeVisible();
      await page.waitForFunction(() => {
        const viewer = document.querySelector("#cv-pdf-viewer");
        return viewer?.getAttribute("data-viewer-ready") === "true";
      });
    },
    url: "/cv?lang=eng",
  },
] satisfies ThemePage[];

for (const pageDefinition of pages) {
  test(`${pageDefinition.name} supports light and dark mode`, async ({ page }, testInfo) => {
    await page.addInitScript(() => {
      window.localStorage.setItem("theme", "latte");
    });

    await page.goto(pageDefinition.url);
    await pageDefinition.ready(page);

    await expectTheme(page, "latte");
    await captureStep(page, testInfo, `${pageDefinition.name}-light`);

    await setTheme(page, "mocha");
    await pageDefinition.ready(page);
    await captureStep(page, testInfo, `${pageDefinition.name}-dark`);

    await setTheme(page, "latte");
    await pageDefinition.ready(page);
    await captureStep(page, testInfo, `${pageDefinition.name}-light-restored`);
  });
}
