import type { Page } from "@playwright/test";
import { captureStep, expect, test } from "../support/agent-test";

type Site = {
  assertionName: string;
  cliCommand: string;
  key: string;
  path: string;
  waitForReady: (page: Page) => Promise<void>;
};

const cliInput = (page: Page) => page.locator('#cle input[name="command"]');

const waitForCommandResponse = (page: Page) =>
  page.waitForResponse((response) => {
    const request = response.request();
    return request.method() === "POST" && new URL(response.url()).pathname === "/command";
  });

const runCliCommand = async (page: Page, command: string): Promise<void> => {
  const input = cliInput(page);

  await expect(input).toBeVisible();
  await input.scrollIntoViewIfNeeded();
  await input.click();
  await input.clear();
  await input.pressSequentially(command);

  const response = waitForCommandResponse(page);
  await input.press("Enter");
  await response;
};

const waitForCvViewerReady = async (page: Page): Promise<void> => {
  await expect(page.locator("#cv-pdf-viewer")).toBeVisible();
  await page.waitForFunction(() => {
    const viewer = document.querySelector("#cv-pdf-viewer");
    return viewer?.getAttribute("data-viewer-ready") === "true";
  });
};

const waitForLanding = async (page: Page): Promise<void> => {
  await expect(page.getByText("System summary")).toBeVisible();
  await expect(page.getByText(">> noahruben").first()).toBeVisible();
};

const waitForProjects = async (page: Page): Promise<void> => {
  await expect(page.locator("#projects-command-preview")).toContainText(">>");
  await expect(page.locator("#projects-command-preview")).toContainText("noahruben projects");
  await expect(page.locator("#search-results")).toBeVisible();
};

const waitForCv = async (page: Page): Promise<void> => {
  await expect(page.locator("h1", { hasText: "CV" })).toBeVisible();
  await waitForCvViewerReady(page);
  await expect(page.locator('canvas[aria-label*="English CV page"]').first()).toBeVisible();
};

const sites: Site[] = [
  {
    assertionName: "landing",
    cliCommand: "noahruben",
    key: "landing",
    path: "/",
    waitForReady: waitForLanding,
  },
  {
    assertionName: "projects",
    cliCommand: "noahruben projects",
    key: "projects",
    path: "/projects",
    waitForReady: waitForProjects,
  },
  {
    assertionName: "cv",
    cliCommand: "noahruben cv",
    key: "cv",
    path: "/cv",
    waitForReady: waitForCv,
  },
];

test("cli navigation smoke covers every site from every site", async ({ page }, testInfo) => {
  for (const origin of sites) {
    for (const target of sites) {
      await page.goto(origin.path);
      await origin.waitForReady(page);
      await expect(cliInput(page)).toBeVisible();

      await runCliCommand(page, target.cliCommand);
      await target.waitForReady(page);
      await expect(cliInput(page)).toHaveValue("");
      await captureStep(page, testInfo, `cli-${origin.key}-to-${target.key}`);
    }
  }
});

test("cli input keeps pending text across pages until Enter is pressed", async ({ page }, testInfo) => {
  for (const site of sites) {
    await page.goto(site.path);
    await site.waitForReady(page);

    const input = cliInput(page);
    const pendingCommand = `${site.cliCommand} help`;

    await expect(input).toBeVisible();
    await input.click();
    await input.fill(pendingCommand);
    await page.waitForTimeout(1000);
    await expect(input).toHaveValue(pendingCommand);

    await captureStep(page, testInfo, `cli-pending-${site.key}`);
  }
});
