import type { Page } from "@playwright/test";
import { captureStep, expect, test } from "../support/agent-test";

async function waitForProjectsIdle(page: Page) {
  await expect(page.locator("#spinner.htmx-request")).toHaveCount(0);
}

test("projects interactions keep the terminal shell in sync", async ({ page }, testInfo) => {
  await page.goto("/projects");

  const projectCards = page.locator("#search-results > div");
  const commandPreview = page.locator("#projects-command-preview");
  const resultsSummary = page.locator("#projects-results-summary");
  const resultsBar = page.locator("#projects-results-bar");
  const queryControl = page.locator("#projects-query-control");
  const queryInput = page.getByLabel("Query");
  const topicControl = page.locator("#projects-topic-control");
  const languageControl = page.locator("#projects-language-control");
  const languageSelect = page.getByLabel("Language");
  const sortControl = page.locator("#projects-sort-control");
  const sortSelect = page.locator("#orderBy");
  const directionToggle = page.locator("#projects-dir-toggle");
  const topicPickerSummary = page.locator("#projects-topic-picker summary");
  const desktopReset = page.locator('[data-reset-context="desktop"]');
  const mobileReset = page.locator('[data-reset-context="mobile"]');

  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);
  await expect(commandPreview).toContainText("noahruben projects");
  await expect(resultsSummary).toContainText("result");
  await expect(resultsBar.locator('[data-reset-context="mobile"]')).toBeHidden();
  await expect(desktopReset).toBeVisible();
  await expect(queryControl).toContainText("--query");
  await expect(topicControl).toContainText("--topic");
  await expect(topicControl).toContainText("all");
  await expect(topicPickerSummary).toHaveText("all");
  await expect(topicControl).not.toContainText("(+)");
  await expect(languageControl).toContainText("--language");
  await expect(languageSelect).toHaveValue("<Language>");
  await expect(sortControl).toContainText("--sort");
  await expect(sortSelect).toHaveValue("Relevance");
  await expect(directionToggle).toHaveText("--asc");
  await expect(mobileReset).toBeHidden();
  await captureStep(page, testInfo, "projects-terminal-initial");

  await queryInput.fill("__definitely_no_matches__");
  await expect(resultsSummary).toHaveText("0 results");
  await expect(queryInput).toHaveValue("__definitely_no_matches__");
  await expect(page.getByText("0 results")).toHaveCount(1);
  await captureStep(page, testInfo, "projects-terminal-no-results");

  await desktopReset.click();
  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);
  await expect(commandPreview).toContainText("noahruben projects");
  await expect(queryInput).toHaveValue("");
  await expect(topicControl).toContainText("all");
  await expect(languageSelect).toHaveValue("<Language>");
  await expect(sortSelect).toHaveValue("Relevance");
  await expect(directionToggle).toHaveText("--asc");

  await topicPickerSummary.click();
  const firstTopicOption = page.locator("#projects-topic-dropdown [data-topic-option]").first();
  const firstTopic = ((await firstTopicOption.textContent()) ?? "").trim();
  expect(firstTopic).not.toEqual("");
  await firstTopicOption.click();
  await expect(page.locator(`[data-selected-topic="${firstTopic}"]`)).toBeVisible();
  await expect(topicPickerSummary).toHaveText("(+)");
  await waitForProjectsIdle(page);

  await topicPickerSummary.click();
  const secondTopicOption = page.locator("#projects-topic-dropdown [data-topic-option]").first();
  const secondTopic = ((await secondTopicOption.textContent()) ?? "").trim();
  expect(secondTopic).not.toEqual("");
  await secondTopicOption.click();
  await expect(topicControl).toContainText(firstTopic);
  await expect(topicControl).toContainText(secondTopic);
  await waitForProjectsIdle(page);

  await page.locator(`[data-selected-topic="${firstTopic}"]`).click();
  await expect(page.locator(`[data-selected-topic="${firstTopic}"]`)).toHaveCount(0);

  const topicTags = page.locator("#search-results [data-topic-tag]");
  const topicTexts = (await topicTags.allTextContents()).map((text) => text.replace(/^topic:/, "").trim());
  const toggledTopic = topicTexts.find((topic) => topic.length > 0 && topic !== secondTopic);
  expect(toggledTopic).toBeTruthy();
  await page.locator(`[data-topic-tag="${toggledTopic}"]`).first().click();
  await expect(page.locator(`[data-selected-topic="${toggledTopic}"]`)).toBeVisible();

  await directionToggle.click();
  await expect(directionToggle).toHaveText("--desc");

  const cardTexts = await projectCards.allTextContents();
  for (const cardText of cardTexts) {
    expect(cardText.includes(secondTopic) || cardText.includes(toggledTopic!)).toBeTruthy();
  }

  await desktopReset.click();
  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);
  await expect(page.locator("[data-selected-topic]")).toHaveCount(0);
  await expect(topicControl).toContainText("all");
  await expect(topicPickerSummary).toHaveText("all");
  await expect(topicControl).not.toContainText("(+)");
  await expect(queryInput).toHaveValue("");
  await expect(languageSelect).toHaveValue("<Language>");
  await expect(sortSelect).toHaveValue("Relevance");
  await expect(directionToggle).toHaveText("--asc");

  await captureStep(page, testInfo, "projects-terminal-filtered");
});

test("projects filters stay visible and usable on mobile", async ({ page }, testInfo) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto("/projects");

  const projectCards = page.locator("#search-results > div");
  const resultsSummary = page.locator("#projects-results-summary");
  const resultsBar = page.locator("#projects-results-bar");
  const queryInput = page.getByLabel("Query");
  const topicPickerSummary = page.locator("#projects-topic-picker summary");
  const desktopReset = page.locator('[data-reset-context="desktop"]');
  const mobileReset = resultsBar.locator('[data-reset-context="mobile"]');

  await expect(page.locator("#projects-command-preview")).toBeVisible();

  const rowBoxes = await Promise.all([
    page.locator('[data-filter-row="topic"]').boundingBox(),
    page.locator('[data-filter-row="language"]').boundingBox(),
    page.locator('[data-filter-row="sort"]').boundingBox(),
    page.locator('[data-filter-row="query"]').boundingBox(),
  ]);

  for (const rowBox of rowBoxes) {
    expect(rowBox).toBeTruthy();
  }

  expect(rowBoxes[0]!.y).toBeLessThan(rowBoxes[1]!.y);
  expect(rowBoxes[1]!.y).toBeLessThan(rowBoxes[2]!.y);
  expect(rowBoxes[2]!.y).toBeLessThan(rowBoxes[3]!.y);

  await expect(page.locator("#projects-query-control")).toBeVisible();
  await expect(page.locator("#projects-topic-control")).toBeVisible();
  await expect(page.locator("#projects-language-control")).toBeVisible();
  await expect(page.locator("#projects-sort-control")).toBeVisible();
  await expect(page.locator("#projects-dir-toggle")).toBeVisible();
  await expect(desktopReset).toBeHidden();
  await expect(mobileReset).toBeVisible();
  await topicPickerSummary.click();
  await expect(page.locator("#projects-topic-dropdown")).toBeVisible();

  const firstTopicOption = page.locator("#projects-topic-dropdown [data-topic-option]").first();
  const firstTopic = ((await firstTopicOption.textContent()) ?? "").trim();
  expect(firstTopic).not.toEqual("");
  await firstTopicOption.click();
  await expect(page.locator(`[data-selected-topic="${firstTopic}"]`)).toBeVisible();
  await waitForProjectsIdle(page);

  await queryInput.fill("__definitely_no_matches__");
  await expect(resultsSummary).toHaveText("0 results");
  await mobileReset.click();
  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);
  await expect(resultsSummary).toContainText("result");
  await expect(page.locator("[data-selected-topic]")).toHaveCount(0);
  await expect(queryInput).toHaveValue("");

  await captureStep(page, testInfo, "projects-terminal-mobile");
});

test("projects reset button clears an unmatched query", async ({ page }, testInfo) => {
  await page.goto("/projects");

  const projectCards = page.locator("#search-results > div");
  const resultsSummary = page.locator("#projects-results-summary");
  const queryInput = page.getByLabel("Query");
  const resetButton = page.locator('[data-reset-context="desktop"]');

  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);

  await queryInput.fill("asdfg");
  await expect(resultsSummary).toHaveText("0 results");
  await expect(queryInput).toHaveValue("asdfg");
  await expect(page.getByText("0 results")).toHaveCount(1);

  await resetButton.click();
  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);
  await expect(resultsSummary).toContainText("result");
  await expect(queryInput).toHaveValue("");

  await captureStep(page, testInfo, "projects-reset-after-asdfg");
});
