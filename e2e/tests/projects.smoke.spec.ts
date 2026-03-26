import { captureStep, expect, test } from "../support/agent-test";

test("projects interactions update visible results", async ({ page }, testInfo) => {
  await page.goto("/projects");

  const projectCards = page.locator("#search-results > div");
  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);
  await captureStep(page, testInfo, "projects-initial");

  await page.getByLabel("Search:").fill("__definitely_no_matches__");
  await expect(page.getByText("Nothing Found")).toBeVisible();
  await captureStep(page, testInfo, "projects-no-results");

  await page.locator('input[value="Reset Search"]').click();
  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);

  const filterTag = page.locator('#search-results [hx-post="/search"]').first();
  const filterText = (await filterTag.textContent())?.trim() ?? "";
  expect(filterText).not.toEqual("");

  await filterTag.click();
  await expect.poll(async () => projectCards.count()).toBeGreaterThan(0);

  const cardTexts = await projectCards.allTextContents();
  for (const cardText of cardTexts) {
    expect(cardText).toContain(filterText);
  }

  await captureStep(page, testInfo, "projects-filtered");
});
