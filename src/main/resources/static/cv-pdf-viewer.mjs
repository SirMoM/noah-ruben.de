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

const PDF_JS_MODULE_URL = `https://cdn.jsdelivr.net/npm/pdfjs-dist@${PDF_JS_VERSION}/build/pdf.min.mjs`;
const PDF_JS_WORKER_URL = `https://cdn.jsdelivr.net/npm/pdfjs-dist@${PDF_JS_VERSION}/build/pdf.worker.min.mjs`;
const CV_PDF_PRELOAD_SELECTOR = "link[data-cv-pdf-preload]";

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
 * Updates the loading copy in the viewer shell.
 *
 * @param {Element | null} element
 * @param {string} text
 */
const updateLoadingText = (element, text) => {
  if (element) {
    element.textContent = text;
  }
};

/**
 * Returns the active CV mode based on the current site theme.
 *
 * @returns {"dark" | "light"}
 */
const getActiveMode = () =>
  document.documentElement.classList.contains("mocha") ? "dark" : "light";

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
    document.head.appendChild(preload);
  }

  preload.href = pdfUrl;
};

/**
 * Computes and stores the exact PDF URL for the active language and theme.
 *
 * @param {Element} root
 * @returns {string | null}
 */
const syncPdfUrl = (root) => {
  const baseUrl = root.getAttribute("data-pdf-url-base");
  if (!baseUrl) {
    return null;
  }

  const url = new URL(baseUrl, window.location.origin);
  url.searchParams.set("mode", getActiveMode());

  const pdfUrl = `${url.pathname}${url.search}`;
  root.setAttribute("data-pdf-url", pdfUrl);
  syncPreload(pdfUrl);
  return pdfUrl;
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
 * Shows the viewer fallback message after a rendering failure.
 *
 * @param {Element | null} loadingElement
 * @param {Element | null} errorElement
 * @param {string} message
 */
const showError = (loadingElement, errorElement, message) => {
  setHidden(loadingElement, true);
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
 * Loads the PDF and paints each page into its pre-rendered Kotlin canvas.
 *
 * @param {Element} root
 * @returns {Promise<void>}
 */
const renderViewer = async (root) => {
  const pdfUrl = syncPdfUrl(root);
  const pdfTitle = root.getAttribute("data-pdf-title") ?? "CV";
  const loadingElement = root.querySelector('[data-role="loading"]');
  const loadingTextElement = root.querySelector('[data-role="loading-text"]');
  const errorElement = root.querySelector('[data-role="error"]');
  const pagesElement = root.querySelector('[data-role="pages"]');

  if (!pdfUrl || !pagesElement) {
    return;
  }

  const canvases = getPageCanvases(pagesElement);
  const renderId = beginRender(root);

  setHidden(pagesElement, true);
  setHidden(errorElement, true);
  setHidden(loadingElement, false);

  try {
    const pdfjs = await import(PDF_JS_MODULE_URL);
    pdfjs.GlobalWorkerOptions.workerSrc = PDF_JS_WORKER_URL;

    updateLoadingText(loadingTextElement, "Downloading document.");

    const loadingTask = pdfjs.getDocument(pdfUrl);
    const pdf = await loadingTask.promise;
    if (isStaleRender(root, renderId)) {
      return;
    }

    if (pdf.numPages > MAX_PAGE_COUNT) {
      throw new Error(
        `Expected at most ${MAX_PAGE_COUNT} PDF pages, received ${pdf.numPages}.`,
      );
    }

    const availableWidth = Math.max(Math.min(root.clientWidth - 32, 920), 320);

    for (const [index, canvas] of canvases.entries()) {
      const pageNumber = index + 1;
      if (pageNumber > pdf.numPages) {
        canvas.setAttribute("hidden", "");
        continue;
      }

      canvas.removeAttribute("hidden");
      updateLoadingText(
        loadingTextElement,
        `Rendering page ${pageNumber} of ${pdf.numPages}.`,
      );

      const page = await pdf.getPage(pageNumber);
      const baseViewport = page.getViewport({ scale: 1 });
      const scale = availableWidth / baseViewport.width;
      const viewport = page.getViewport({ scale });
      const outputScale = window.devicePixelRatio || 1;
      const transform =
        outputScale === 1 ? null : [outputScale, 0, 0, outputScale, 0, 0];
      const context = canvas.getContext("2d");

      if (!context) {
        throw new Error("Canvas 2D context is unavailable.");
      }

      configureCanvas(canvas, viewport, outputScale);
      canvas.setAttribute("aria-label", `${pdfTitle} page ${pageNumber}`);

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
    }

    if (isStaleRender(root, renderId)) {
      return;
    }

    setHidden(loadingElement, true);
    setHidden(errorElement, true);
    setHidden(pagesElement, false);
  } catch (error) {
    if (isStaleRender(root, renderId)) {
      return;
    }

    console.error("Failed to render CV PDF preview.", error);
    showError(
      loadingElement,
      errorElement,
      "The PDF preview is unavailable right now.",
    );
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
