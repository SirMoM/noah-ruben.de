import { captureStep, expect, test } from "../support/agent-test";

test("projects interactions keep the terminal shell in sync", async ({ page }, testInfo) => {
  await page.goto("/projects");

  const projectCards = page.locator("#search-results > div");
  const commandPreview = page.locator("#projects-command-preview");
  const resultsSummary = page.locator("#projects-results-summary");
  const queryControl = page.locator("#projects-query-control");
  const queryInput = page.getByLabel("Query");
  const topicControl = page.locator("#projects-topic-control");
  const languageControl = page.locator("#projects-language-control");
  const languageSelect = page.getByLabel("Language");
  const sortControl = page.locator("#projects-sort-control");
  const sortSelect = page.locator("#orderBy");
  const directionToggle = page.locator("#projects-dir-toggle");
  const topicPickerSummary = page.locator("#projects-topic-picker summary");

  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);
  await expect(commandPreview).toContainText("noahruben projects");
  await expect(resultsSummary).toContainText("results");
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
  await captureStep(page, testInfo, "projects-terminal-initial");

  await queryInput.fill("__definitely_no_matches__");
  await expect(resultsSummary).toHaveText("0 results");
  await expect(queryInput).toHaveValue("__definitely_no_matches__");
  await expect(page.getByText("0 results").first()).toBeVisible();
  await captureStep(page, testInfo, "projects-terminal-no-results");

  await page.getByRole("button", { name: "[reset filters]" }).first().click();
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

  await topicPickerSummary.click();
  const secondTopicOption = page.locator("#projects-topic-dropdown [data-topic-option]").first();
  const secondTopic = ((await secondTopicOption.textContent()) ?? "").trim();
  expect(secondTopic).not.toEqual("");
  await secondTopicOption.click();
  await expect(topicControl).toContainText(firstTopic);
  await expect(topicControl).toContainText(secondTopic);

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

  await page.getByRole("button", { name: "[reset filters]" }).first().click();
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

test("projects filters stay visible on mobile", async ({ page }, testInfo) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto("/projects");

  await expect(page.locator("#projects-command-preview")).toBeVisible();
  await expect(page.locator("#projects-query-control")).toBeVisible();
  await expect(page.locator("#projects-topic-control")).toBeVisible();
  await expect(page.locator("#projects-language-control")).toBeVisible();
  await expect(page.locator("#projects-sort-control")).toBeVisible();
  await expect(page.locator("#projects-dir-toggle")).toBeVisible();
  await page.locator("#projects-topic-picker summary").click();
  await expect(page.locator("#projects-topic-dropdown")).toBeVisible();

  await captureStep(page, testInfo, "projects-terminal-mobile");
});

test("projects reset button clears an unmatched query", async ({ page }, testInfo) => {
  await page.goto("/projects");

  const projectCards = page.locator("#search-results > div");
  const resultsSummary = page.locator("#projects-results-summary");
  const queryInput = page.getByLabel("Query");
  const resetButton = page.getByRole("button", { name: "[reset filters]" }).first();

  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);

  await queryInput.fill("asdfg");
  await expect(resultsSummary).toHaveText("0 results");
  await expect(queryInput).toHaveValue("asdfg");

  await resetButton.click();
  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);
  await expect(resultsSummary).toContainText("results");
  await expect(queryInput).toHaveValue("");

  await captureStep(page, testInfo, "projects-reset-after-asdfg");
});
