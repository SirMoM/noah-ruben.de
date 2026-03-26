import { defineConfig } from "@playwright/test";

const baseURL = process.env.BASE_URL ?? "http://127.0.0.1:42081";

export default defineConfig({
  testDir: "./tests",
  timeout: 60_000,
  expect: {
    timeout: 10_000,
  },
  fullyParallel: false,
  outputDir: "./test-results/playwright",
  reporter: [
    ["list"],
    ["html", { open: "never", outputFolder: "./playwright-report" }],
  ],
  projects: [
    {
      name: "chromium",
      use: {
        browserName: "chromium",
      },
    },
    {
      name: "cv-video",
      testMatch: /cv\.smoke\.spec\.ts/,
      use: {
        browserName: "chromium",
        video: "on",
      },
    },
  ],
  retries: 0,
  workers: 1,
  use: {
    baseURL,
    ignoreHTTPSErrors: true,
    screenshot: "off",
    trace: "on",
    video: "retain-on-failure",
    viewport: {
      width: 1440,
      height: 1100,
    },
  },
});
