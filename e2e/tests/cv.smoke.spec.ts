import type { Page } from "@playwright/test";
import { captureStep, expect, setTheme, test } from "../support/agent-test";

type ViewerState = {
  language: "eng" | "ger";
  mode: "light" | "dark";
  titleFragment: "English" | "German";
};

type TextSelectionResult = {
  selectionRectCount: number;
  selectionRectWidth: number;
  selectedText: string;
  targetRectWidth: number;
  spanCount: number;
  textLayerText: string;
};

const getViewer = (page: Page) => page.locator("#cv-pdf-viewer");

const viewerReady = async (page: Page): Promise<string | null> =>
  getViewer(page).getAttribute("data-viewer-ready");

const viewerPdfUrl = async (page: Page): Promise<string | null> =>
  getViewer(page).getAttribute("data-pdf-url");

const viewerRenderId = async (page: Page): Promise<number> =>
  Number((await getViewer(page).getAttribute("data-render-id")) ?? "0");

const expectCvPrompt = async (page: Page, language: "eng" | "ger"): Promise<void> => {
  await expect(page.locator('[data-role="cv-command-text"]')).toHaveText(`noahruben cv ${language}`);
};

const waitForViewerReady = async (page: Page): Promise<void> => {
  await page.waitForFunction(() => {
    const viewer = document.querySelector("#cv-pdf-viewer");
    return viewer?.getAttribute("data-viewer-ready") === "true";
  });
};

const expectViewerState = async (page: Page, { language, mode, titleFragment }: ViewerState): Promise<void> => {
  const viewer = getViewer(page);

  await expect(viewer).toHaveAttribute("data-current-language", language);
  await waitForViewerReady(page);
  await expect.poll(() => viewerReady(page)).toBe("true");
  await expect.poll(() => viewerPdfUrl(page)).toContain(`lang=${language}`);
  await expect.poll(() => viewerPdfUrl(page)).toContain(`mode=${mode}`);
  await expect(viewer.locator('[data-role="loading"]')).toBeHidden();
  await expect(viewer.locator('[data-role="error"]')).toBeHidden();
  await expect(page.locator(`canvas[aria-label*="${titleFragment} CV page"]`).first()).toBeVisible();
};

const selectExactTextFromFirstCvPage = async (
  page: Page,
  targetText: string,
): Promise<TextSelectionResult> =>
  page.locator('[data-role="text-layer"]').first().evaluate((layer, expectedText): TextSelectionResult => {
    const textLayer = layer as HTMLElement;
    const spans = Array.from(textLayer.querySelectorAll("span")).filter(
      (span): span is HTMLSpanElement =>
        Boolean(span.firstChild && span.textContent && span.textContent.trim().length > 0),
    );
    const targetSpan = spans.find((span) => span.textContent?.trim() === expectedText);

    if (spans.length === 0 || !targetSpan) {
      return {
        selectionRectCount: 0,
        selectionRectWidth: 0,
        selectedText: "",
        targetRectWidth: 0,
        spanCount: 0,
        textLayerText: textLayer.textContent ?? "",
      };
    }

    const textNode = targetSpan.firstChild;
    const selection = window.getSelection();

    if (!textNode || !selection) {
      return {
        selectionRectCount: 0,
        selectionRectWidth: 0,
        selectedText: "",
        targetRectWidth: 0,
        spanCount: spans.length,
        textLayerText: textLayer.textContent ?? "",
      };
    }

    const range = document.createRange();

    selection.removeAllRanges();
    range.setStart(textNode, 0);
    range.setEnd(textNode, textNode.textContent?.length ?? 0);
    selection.addRange(range);

    const selectionRects = Array.from(range.getClientRects()).filter(
      (rect) => rect.width > 0 && rect.height > 0,
    );
    const targetRect = targetSpan.getBoundingClientRect();

    return {
      selectionRectCount: selectionRects.length,
      selectionRectWidth: selectionRects[0]?.width ?? 0,
      selectedText: selection.toString(),
      targetRectWidth: targetRect.width,
      spanCount: spans.length,
      textLayerText: textLayer.textContent ?? "",
    };
  }, targetText);

test("cv viewer loads and reacts to language and theme changes", async ({ page }, testInfo) => {
  await page.addInitScript(() => {
    window.localStorage.setItem("theme", "latte");
  });

  await page.goto("/cv?lang=eng");

  const viewer = getViewer(page);

  await expectCvPrompt(page, "eng");
  await expect(viewer).toBeVisible();
  await expectViewerState(page, {
    language: "eng",
    mode: "light",
    titleFragment: "English",
  });
  await captureStep(page, testInfo, "cv-english-light");

  const cli = page.locator("#cle");
  await cli.scrollIntoViewIfNeeded();
  await expect(cli).toBeVisible();
  await expect(cli.locator('input[name="command"]')).toBeVisible();
  await captureStep(page, testInfo, "cv-cli-footer");

  const renderIdBeforeDarkMode = await viewerRenderId(page);

  await setTheme(page, "mocha");
  await expectViewerState(page, {
    language: "eng",
    mode: "dark",
    titleFragment: "English",
  });
  await expect.poll(() => viewerRenderId(page)).toBeGreaterThan(renderIdBeforeDarkMode);
  await captureStep(page, testInfo, "cv-english-dark");

  await page.getByRole("link", { name: "Deutsch" }).click();
  await expect(page).toHaveURL(/\/cv\?lang=ger$/);
  await expectViewerState(page, {
    language: "ger",
    mode: "dark",
    titleFragment: "German",
  });
  await expectCvPrompt(page, "ger");
  await captureStep(page, testInfo, "cv-german-dark");

  const renderIdBeforeLightMode = await viewerRenderId(page);
  await setTheme(page, "latte");
  await expectViewerState(page, {
    language: "ger",
    mode: "light",
    titleFragment: "German",
  });
  await expectCvPrompt(page, "ger");
  await expect.poll(() => viewerRenderId(page)).toBeGreaterThan(renderIdBeforeLightMode);
  await captureStep(page, testInfo, "cv-german-light");

  await page.waitForFunction(() =>
    Array.from(document.querySelectorAll('[data-role="text-layer"] span')).some(
      (span) => span.textContent?.trim() === "Profil",
    ),
  );

  const textSelection = await selectExactTextFromFirstCvPage(page, "Profil");
  expect(textSelection.spanCount).toBeGreaterThan(0);
  expect(textSelection.textLayerText.trim().length).toBeGreaterThan(20);
  expect(textSelection.selectedText.trim()).toBe("Profil");
  expect(textSelection.selectionRectCount).toBe(1);
  expect(
    Math.abs(textSelection.selectionRectWidth - textSelection.targetRectWidth),
  ).toBeLessThanOrEqual(2);
  await captureStep(page, testInfo, "cv-text-selection");

  await page.evaluate(() => window.getSelection()?.removeAllRanges());

  await page.getByRole("link", { name: "English" }).click();
  await expect(page).toHaveURL(/\/cv\?lang=eng$/);
  await expectViewerState(page, {
    language: "eng",
    mode: "light",
    titleFragment: "English",
  });
  await expectCvPrompt(page, "eng");
  await captureStep(page, testInfo, "cv-returned-to-english-light");
});

test("cv viewer shows a visible error when pdf loading fails", async ({ page }, testInfo) => {
  await page.route("**/cv/pdf?**", (route) => route.abort());

  await page.goto("/cv?lang=eng");

  const viewer = page.locator("#cv-pdf-viewer");
  const errorMessage = viewer.locator('[data-role="error"]');
  const cli = page.locator("#cle");

  await expectCvPrompt(page, "eng");
  await expect(errorMessage).toBeVisible();
  await expect(errorMessage).toContainText("The PDF preview is unavailable right now.");
  await expect(viewer).toHaveAttribute("data-viewer-ready", "false");
  await expect(cli).toBeVisible();
  await captureStep(page, testInfo, "cv-load-error");
});
