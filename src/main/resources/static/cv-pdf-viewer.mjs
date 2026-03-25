/**
 * Renders the CV viewer using PDF.js.
 *
 * Contract:
 * - Kotlin provides `#cv-pdf-viewer` with `data-pdf-url-base` and `data-pdf-title`.
 * - Kotlin pre-renders exactly three `<canvas>` elements for the CV pages.
 * - This module computes the active PDF URL, sizes the canvases, and paints them.
 */
const PDF_JS_VERSION = "4.0.379";
const MAX_PAGE_COUNT = 3;
const PREFETCH_IDLE_TIMEOUT_MS = 1500;

const PDF_JS_MODULE_URL = `https://cdn.jsdelivr.net/npm/pdfjs-dist@${PDF_JS_VERSION}/build/pdf.min.mjs`;
const PDF_JS_WORKER_URL = `https://cdn.jsdelivr.net/npm/pdfjs-dist@${PDF_JS_VERSION}/build/pdf.worker.min.mjs`;
const CV_PDF_PRELOAD_SELECTOR = "link[data-cv-pdf-preload]";
const warmedPdfUrls = new WeakMap();
const queuedPrefetchUrls = new WeakMap();

const logInfo = (...parts) => {
  console.info("[cv-pdf-viewer]", ...parts);
};

const logWarn = (...parts) => {
  console.warn("[cv-pdf-viewer]", ...parts);
};

const logError = (...parts) => {
  console.error("[cv-pdf-viewer]", ...parts);
};

/**
 * Toggles an element's hidden state when the element exists.
 *
 * @param {Element | null} element
 * @param {boolean} hidden
 */
const setHidden = (element, hidden) => {
  if (!element) {
    return;
  }

  if (hidden) {
    element.setAttribute("hidden", "");
  } else {
    element.removeAttribute("hidden");
  }
};

/**
 * Returns the tracked URL set for a viewer root.
 *
 * @param {WeakMap<Element, Set<string>>} map
 * @param {Element} root
 * @returns {Set<string>}
 */
const getTrackedUrlSet = (map, root) => {
  let urls = map.get(root);
  if (!urls) {
    urls = new Set();
    map.set(root, urls);
  }

  return urls;
};

/**
 * Returns the active CV mode based on the current site theme.
 *
 * @returns {"dark" | "light"}
 */
const getActiveMode = () =>
  document.documentElement.classList.contains("mocha") ? "dark" : "light";

/**
 * Returns the inactive CV mode for the current theme.
 *
 * @param {"dark" | "light"} mode
 * @returns {"dark" | "light"}
 */
const getAlternateMode = (mode) => (mode === "dark" ? "light" : "dark");

/**
 * Returns a concrete PDF URL for the supplied base URL and mode.
 *
 * @param {string} baseUrl
 * @param {"dark" | "light"} mode
 * @returns {string}
 */
const buildPdfUrl = (baseUrl, mode) => {
  const url = new URL(baseUrl, window.location.origin);
  url.searchParams.set("mode", mode);
  return `${url.pathname}${url.search}`;
};

/**
 * Updates the PDF preload hint when it exists or creates it when missing.
 *
 * @param {string} pdfUrl
 */
const syncPreload = (pdfUrl) => {
  let preload = document.querySelector(CV_PDF_PRELOAD_SELECTOR);
  if (!(preload instanceof HTMLLinkElement)) {
    preload = document.createElement("link");
    preload.setAttribute("data-cv-pdf-preload", "");
    preload.rel = "preload";
    preload.as = "fetch";
    preload.setAttribute("fetchpriority", "high");
    document.head.appendChild(preload);
  }

  preload.href = pdfUrl;
};

/**
 * Schedules a callback once the browser is idle or a timeout expires.
 *
 * @param {() => void} callback
 */
const scheduleWhenIdle = (callback) => {
  if ("requestIdleCallback" in window) {
    window.requestIdleCallback(callback, {
      timeout: PREFETCH_IDLE_TIMEOUT_MS,
    });
    return;
  }

  window.setTimeout(callback, PREFETCH_IDLE_TIMEOUT_MS);
};

/**
 * Queues an alternate-theme PDF fetch in the background.
 *
 * @param {Element} root
 * @param {string | null} alternatePdfUrl
 */
const queueAlternateThemePrefetch = (root, alternatePdfUrl) => {
  if (!alternatePdfUrl) {
    return;
  }

  const warmedUrls = getTrackedUrlSet(warmedPdfUrls, root);
  if (warmedUrls.has(alternatePdfUrl)) {
    return;
  }

  const queuedUrls = getTrackedUrlSet(queuedPrefetchUrls, root);
  if (queuedUrls.has(alternatePdfUrl)) {
    return;
  }

  queuedUrls.add(alternatePdfUrl);
  logInfo("Queued alternate-theme prefetch.", alternatePdfUrl);

  scheduleWhenIdle(async () => {
    if (root.getAttribute("data-alt-pdf-url") !== alternatePdfUrl) {
      queuedUrls.delete(alternatePdfUrl);
      logInfo("Skipped stale alternate-theme prefetch.", alternatePdfUrl);
      return;
    }

    logInfo("Starting alternate-theme prefetch.", alternatePdfUrl);

    try {
      const response = await fetch(alternatePdfUrl, {
        cache: "force-cache",
      });

      if (!response.ok) {
        throw new Error(`Unexpected prefetch status ${response.status}.`);
      }

      await response.arrayBuffer();
      warmedUrls.add(alternatePdfUrl);
      logInfo("Alternate-theme prefetch completed.", alternatePdfUrl);
    } catch (error) {
      logWarn("Alternate-theme prefetch failed.", alternatePdfUrl, error);
    } finally {
      queuedUrls.delete(alternatePdfUrl);
    }
  });
};

/**
 * Computes and stores the exact PDF URLs for the active language and theme.
 *
 * @param {Element} root
 * @returns {{ pdfUrl: string, alternatePdfUrl: string } | null}
 */
const syncPdfUrls = (root) => {
  const baseUrl = root.getAttribute("data-pdf-url-base");
  if (!baseUrl) {
    return null;
  }

  const activeMode = getActiveMode();
  const pdfUrl = buildPdfUrl(baseUrl, activeMode);
  const alternatePdfUrl = buildPdfUrl(baseUrl, getAlternateMode(activeMode));
  root.setAttribute("data-pdf-url", pdfUrl);
  root.setAttribute("data-alt-pdf-url", alternatePdfUrl);
  syncPreload(pdfUrl);
  logInfo("Synced PDF URLs.", {
    active: pdfUrl,
    alternate: alternatePdfUrl,
  });
  return {
    pdfUrl,
    alternatePdfUrl,
  };
};

/**
 * Applies the rendered page dimensions to a Kotlin-provided canvas.
 *
 * @param {HTMLCanvasElement} canvas
 * @param {{ width: number, height: number }} viewport
 * @param {number} outputScale
 */
const configureCanvas = (canvas, viewport, outputScale) => {
  canvas.width = Math.floor(viewport.width * outputScale);
  canvas.height = Math.floor(viewport.height * outputScale);
  canvas.style.width = `${Math.floor(viewport.width)}px`;
  canvas.style.height = `${Math.floor(viewport.height)}px`;
  canvas.style.display = "block";
  canvas.style.maxWidth = "100%";
};

/**
 * Returns true once the viewer has rendered at least one document.
 *
 * @param {Element} root
 * @returns {boolean}
 */
const isViewerReady = (root) => root.getAttribute("data-viewer-ready") === "true";

/**
 * Shows the viewer fallback message after a rendering failure.
 *
 * @param {Element | null} pagesElement
 * @param {Element | null} errorElement
 * @param {string} message
 */
const showError = (pagesElement, errorElement, message) => {
  setHidden(pagesElement, true);
  if (errorElement) {
    errorElement.textContent = message;
  }
  setHidden(errorElement, false);
};

/**
 * Returns the Kotlin-rendered canvas slots required by the viewer contract.
 *
 * @param {Element} pagesElement
 * @returns {HTMLCanvasElement[]}
 */
const getPageCanvases = (pagesElement) => {
  const canvases = Array.from(
    pagesElement.querySelectorAll("canvas[data-page-number]"),
  );

  if (canvases.length !== MAX_PAGE_COUNT) {
    throw new Error(
      `Expected ${MAX_PAGE_COUNT} canvas elements, received ${canvases.length}.`,
    );
  }

  return canvases;
};

/**
 * Starts a new render cycle for a viewer root and returns its unique render id.
 *
 * @param {Element} root
 * @returns {string}
 */
const beginRender = (root) => {
  const renderId = `${Number.parseInt(root.getAttribute("data-render-id") ?? "0", 10) + 1}`;
  root.setAttribute("data-render-id", renderId);
  return renderId;
};

/**
 * Returns true when a newer render cycle has already replaced this one.
 *
 * @param {Element} root
 * @param {string} renderId
 * @returns {boolean}
 */
const isStaleRender = (root, renderId) =>
  root.getAttribute("data-render-id") !== renderId;

/**
 * Paints a fully rendered buffer canvas into the viewer's visible canvas slot.
 *
 * @param {HTMLCanvasElement} canvas
 * @param {HTMLCanvasElement} bufferCanvas
 * @param {{ width: number, height: number }} viewport
 * @param {number} outputScale
 * @param {string} pdfTitle
 * @param {number} pageNumber
 */
const applyRenderedPage = (
  canvas,
  bufferCanvas,
  viewport,
  outputScale,
  pdfTitle,
  pageNumber,
) => {
  const context = canvas.getContext("2d");
  if (!context) {
    throw new Error("Canvas 2D context is unavailable.");
  }

  configureCanvas(canvas, viewport, outputScale);
  context.clearRect(0, 0, canvas.width, canvas.height);
  context.drawImage(bufferCanvas, 0, 0);
  canvas.setAttribute("aria-label", `${pdfTitle} page ${pageNumber}`);
};

/**
 * Loads the PDF and paints each page into its pre-rendered Kotlin canvas.
 *
 * @param {Element} root
 * @returns {Promise<void>}
 */
const renderViewer = async (root) => {
  const urls = syncPdfUrls(root);
  const pdfTitle = root.getAttribute("data-pdf-title") ?? "CV";
  const errorElement = root.querySelector('[data-role="error"]');
  const pagesElement = root.querySelector('[data-role="pages"]');

  if (!urls || !pagesElement) {
    return;
  }

  const { pdfUrl, alternatePdfUrl } = urls;
  const canvases = getPageCanvases(pagesElement);
  const renderId = beginRender(root);
  const shouldHidePages = !isViewerReady(root);

  if (shouldHidePages) {
    setHidden(pagesElement, true);
  }
  setHidden(errorElement, true);

  try {
    logInfo("Loading PDF.js module.");
    const pdfjs = await import(PDF_JS_MODULE_URL);
    pdfjs.GlobalWorkerOptions.workerSrc = PDF_JS_WORKER_URL;

    logInfo("Starting active PDF download.", pdfUrl);
    const loadingTask = pdfjs.getDocument(pdfUrl);
    queueAlternateThemePrefetch(root, alternatePdfUrl);
    const pdf = await loadingTask.promise;
    getTrackedUrlSet(warmedPdfUrls, root).add(pdfUrl);
    logInfo("Active PDF ready.", {
      url: pdfUrl,
      pages: pdf.numPages,
    });

    if (isStaleRender(root, renderId)) {
      return;
    }

    if (pdf.numPages > MAX_PAGE_COUNT) {
      throw new Error(
        `Expected at most ${MAX_PAGE_COUNT} PDF pages, received ${pdf.numPages}.`,
      );
    }

    const availableWidth = Math.max(Math.min(root.clientWidth - 32, 920), 320);
    const renderedPages = [];

    for (const [index, canvas] of canvases.entries()) {
      const pageNumber = index + 1;
      if (pageNumber > pdf.numPages) {
        renderedPages.push(null);
        continue;
      }

      logInfo(`Rendering page ${pageNumber} of ${pdf.numPages}.`);

      const page = await pdf.getPage(pageNumber);
      const baseViewport = page.getViewport({ scale: 1 });
      const scale = availableWidth / baseViewport.width;
      const viewport = page.getViewport({ scale });
      const outputScale = window.devicePixelRatio || 1;
      const transform =
        outputScale === 1 ? null : [outputScale, 0, 0, outputScale, 0, 0];
      const bufferCanvas = document.createElement("canvas");
      const context = bufferCanvas.getContext("2d");

      if (!context) {
        throw new Error("Canvas 2D context is unavailable.");
      }

      configureCanvas(bufferCanvas, viewport, outputScale);

      const renderContext = {
        canvasContext: context,
        viewport,
        transform,
      };

      await page.render(renderContext).promise;
      if (isStaleRender(root, renderId)) {
        return;
      }

      page.cleanup();
      renderedPages.push({
        bufferCanvas,
        outputScale,
        pageNumber,
        viewport,
      });
    }

    for (const [index, canvas] of canvases.entries()) {
      const renderedPage = renderedPages[index];
      if (!renderedPage) {
        canvas.setAttribute("hidden", "");
        continue;
      }

      canvas.removeAttribute("hidden");
      applyRenderedPage(
        canvas,
        renderedPage.bufferCanvas,
        renderedPage.viewport,
        renderedPage.outputScale,
        pdfTitle,
        renderedPage.pageNumber,
      );
    }

    if (isStaleRender(root, renderId)) {
      return;
    }

    root.setAttribute("data-viewer-ready", "true");
    setHidden(errorElement, true);
    setHidden(pagesElement, false);
    logInfo("Viewer render completed.", pdfUrl);
  } catch (error) {
    if (isStaleRender(root, renderId)) {
      return;
    }

    logError("Failed to render CV PDF preview.", error);

    if (isViewerReady(root)) {
      logWarn("Keeping the last rendered PDF visible after the failure.");
      return;
    }

    showError(pagesElement, errorElement, "The PDF preview is unavailable right now.");
  }
};

const viewerRoots = Array.from(document.querySelectorAll("#cv-pdf-viewer"));

viewerRoots.forEach((root) => {
  void renderViewer(root);
});

if (viewerRoots.length > 0) {
  let rerenderQueued = false;
  const queueRerender = () => {
    if (rerenderQueued) {
      return;
    }

    rerenderQueued = true;
    window.requestAnimationFrame(() => {
      rerenderQueued = false;
      viewerRoots.forEach((root) => {
        void renderViewer(root);
      });
    });
  };

  new MutationObserver((mutations) => {
    if (mutations.some((mutation) => mutation.attributeName === "class")) {
      queueRerender();
    }
  }).observe(document.documentElement, {
    attributes: true,
    attributeFilter: ["class"],
  });
}
