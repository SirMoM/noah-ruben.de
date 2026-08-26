import { writeFile } from "node:fs/promises";
import {
  expect,
  test as base,
  type ConsoleMessage,
  type Page,
  type PageScreenshotOptions,
  type TestInfo,
} from "@playwright/test";

const slug = (value: string): string =>
  value
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-|-$/g, "");

const attachJson = async (testInfo: TestInfo, name: string, value: unknown): Promise<void> => {
  const filePath = testInfo.outputPath(name);
  await writeFile(filePath, `${JSON.stringify(value, null, 2)}\n`);
  await testInfo.attach(name, {
    path: filePath,
    contentType: "application/json",
  });
};

const captureStep = async (
  page: Page,
  testInfo: TestInfo,
  name: string,
  options: PageScreenshotOptions = {},
): Promise<void> => {
  if (page.isClosed()) {
    return;
  }

  const filePath = testInfo.outputPath(`${slug(name)}.png`);
  await page.screenshot({
    fullPage: true,
    path: filePath,
    ...options,
  });
  await testInfo.attach(name, {
    path: filePath,
    contentType: "image/png",
  });
};

const currentTheme = async (page: Page): Promise<"mocha" | "latte"> =>
  page.evaluate(() =>
    document.documentElement.classList.contains("mocha") ? "mocha" : "latte",
  );

const expectTheme = async (page: Page, theme: "mocha" | "latte"): Promise<void> => {
  const themeButton = page.getByRole("button", { name: "Toggle dark mode" });
  const isDark = theme === "mocha";

  await expect.poll(() => currentTheme(page)).toBe(theme);
  await expect(themeButton).toHaveAttribute("aria-pressed", String(isDark));
  await expect(themeButton).toHaveAttribute("data-theme", isDark ? "dark" : "light");
};

const setTheme = async (page: Page, theme: "mocha" | "latte"): Promise<void> => {
  const current = await currentTheme(page);
  if (current !== theme) {
    await page.getByRole("button", { name: "Toggle dark mode" }).click();
  }
  await expectTheme(page, theme);
};

type ConsoleEntry = {
  location: ReturnType<ConsoleMessage["location"]>;
  text: string;
  type: string;
};

type PageErrorEntry = {
  message: string;
  stack: string | undefined;
};

type NetworkEntry = {
  failure?: string | null;
  method: string;
  resourceType: string;
  status?: number;
  type: "requestfailed" | "response";
  url: string;
};

const test = base.extend({
  page: async ({ page }, use, testInfo) => {
    const consoleEntries: ConsoleEntry[] = [];
    const networkEntries: NetworkEntry[] = [];
    const pageErrors: PageErrorEntry[] = [];

    page.on("console", (message) => {
      consoleEntries.push({
        location: message.location(),
        text: message.text(),
        type: message.type(),
      });
    });

    page.on("pageerror", (error) => {
      pageErrors.push({
        message: error.message,
        stack: error.stack,
      });
    });

    page.on("requestfailed", (request) => {
      networkEntries.push({
        failure: request.failure()?.errorText ?? null,
        method: request.method(),
        resourceType: request.resourceType(),
        type: "requestfailed",
        url: request.url(),
      });
    });

    page.on("response", (response) => {
      const request = response.request();
      networkEntries.push({
        method: request.method(),
        resourceType: request.resourceType(),
        status: response.status(),
        type: "response",
        url: response.url(),
      });
    });

    await use(page);

    await attachJson(testInfo, "console-log.json", consoleEntries);
    await attachJson(testInfo, "network-log.json", networkEntries);
    await attachJson(testInfo, "page-errors.json", pageErrors);
    await captureStep(page, testInfo, "final-state");
  },
});

export {
  captureStep,
  currentTheme,
  expect,
  expectTheme,
  setTheme,
  test,
};
