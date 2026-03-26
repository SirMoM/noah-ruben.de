const PDF_JS_VERSION = "4.0.379";
const MAX_PAGE_COUNT = 3;
const PDF_JS_MODULE_URL =
  `https://cdn.jsdelivr.net/npm/pdfjs-dist@${PDF_JS_VERSION}/build/pdf.min.mjs`;
const PDF_JS_WORKER_URL =
  `https://cdn.jsdelivr.net/npm/pdfjs-dist@${PDF_JS_VERSION}/build/pdf.worker.min.mjs`;

let pdfJsPromise = null;

const renderCaches = new WeakMap();

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

const getRenderWidth = (root) =>
  Math.max(Math.min(root.clientWidth - 32, 920), 320);

const buildPdfUrl = (baseUrl, mode) => {
  const url = new URL(baseUrl, window.location.origin);
  url.searchParams.set("mode", mode);
  return `${url.pathname}${url.search}`;
};

const configureCanvas = (canvas, viewport, outputScale) => {
  canvas.width = Math.floor(viewport.width * outputScale);
  canvas.height = Math.floor(viewport.height * outputScale);
  canvas.style.width = `${Math.floor(viewport.width)}px`;
  canvas.style.height = `${Math.floor(viewport.height)}px`;
};

const getPageSlots = (pagesElement) => {
  const slots = Array.from(pagesElement.querySelectorAll('[data-role="page-slot"]'));

  if (slots.length !== MAX_PAGE_COUNT) {
    throw new Error(
      `Expected ${MAX_PAGE_COUNT} page slots, received ${slots.length}.`,
    );
  }

  return slots.map((slot) => {
    const canvas = slot.querySelector("canvas[data-page-number]");
    const textLayer = slot.querySelector('[data-role="text-layer"]');

    if (!(canvas instanceof HTMLCanvasElement) || !(textLayer instanceof HTMLDivElement)) {
      throw new Error("Each page slot must contain a canvas and a text layer.");
    }

    return {
      canvas,
      slot,
      textLayer,
    };
  });
};

const paintPage = (canvas, renderedPage, pdfTitle) => {
  const context = canvas.getContext("2d");
  if (!context) {
    throw new Error("Canvas 2D context is unavailable.");
  }

  configureCanvas(canvas, renderedPage.viewport, renderedPage.outputScale);
  context.clearRect(0, 0, canvas.width, canvas.height);
  context.drawImage(renderedPage.bufferCanvas, 0, 0);
  canvas.setAttribute(
    "aria-label",
    `${pdfTitle} page ${renderedPage.pageNumber}`,
  );
};

const clearPageSlot = ({ slot, canvas, textLayer }) => {
  setHidden(slot, true);
  slot.style.removeProperty("height");
  slot.style.removeProperty("width");
  canvas.removeAttribute("aria-label");
  textLayer.replaceChildren();
  textLayer.style.removeProperty("--total-scale-factor");
};

const loadPdfJs = async () => {
  if (!pdfJsPromise) {
    pdfJsPromise = import(PDF_JS_MODULE_URL).then((pdfjs) => {
      pdfjs.GlobalWorkerOptions.workerSrc = PDF_JS_WORKER_URL;
      return pdfjs;
    });
  }

  return pdfJsPromise;
};

const getRenderCache = (root) => {
  let cache = renderCaches.get(root);
  if (!cache) {
    cache = new Map();
    renderCaches.set(root, cache);
  }

  return cache;
};

const renderTextLayer = async (pdfjs, textLayer, renderedPage) => {
  textLayer.replaceChildren();
  textLayer.style.setProperty(
    "--total-scale-factor",
    renderedPage.viewport.scale.toString(),
  );

  const task = pdfjs.renderTextLayer({
    container: textLayer,
    isOffscreenCanvasSupported: true,
    textContentItemsStr: [],
    textContentSource: renderedPage.textContent,
    textDivProperties: new WeakMap(),
    textDivs: [],
    viewport: renderedPage.viewport,
  });
  await task.promise;
};

export const buildVariant = (root, link, mode) => {
  if (!(link instanceof HTMLAnchorElement)) {
    return null;
  }

  const baseUrl = link.getAttribute("data-pdf-url-base");
  const language = link.getAttribute("data-cv-language");
  if (!baseUrl || !language) {
    return null;
  }

  const width = getRenderWidth(root);
  const outputScale = window.devicePixelRatio || 1;

  return {
    baseUrl,
    key: [language, mode, width, outputScale].join("|"),
    language,
    mode,
    outputScale,
    pageUrl: link.href,
    pdfTitle: link.getAttribute("data-pdf-title") ?? "CV",
    pdfUrl: buildPdfUrl(baseUrl, mode),
    width,
  };
};

const renderVariant = async (variant) => {
  const pdfjs = await loadPdfJs();
  const pdf = await pdfjs.getDocument(variant.pdfUrl).promise;

  if (pdf.numPages > MAX_PAGE_COUNT) {
    throw new Error(
      `Expected at most ${MAX_PAGE_COUNT} PDF pages, received ${pdf.numPages}.`,
    );
  }

  const renderedPages = [];

  for (let pageNumber = 1; pageNumber <= MAX_PAGE_COUNT; pageNumber += 1) {
    if (pageNumber > pdf.numPages) {
      renderedPages.push(null);
      continue;
    }

    const page = await pdf.getPage(pageNumber);
    const baseViewport = page.getViewport({ scale: 1 });
    const scale = variant.width / baseViewport.width;
    const viewport = page.getViewport({ scale });
    const transform =
      variant.outputScale === 1
        ? null
        : [variant.outputScale, 0, 0, variant.outputScale, 0, 0];
    const bufferCanvas = document.createElement("canvas");
    const context = bufferCanvas.getContext("2d");

    if (!context) {
      throw new Error("Canvas 2D context is unavailable.");
    }

    configureCanvas(bufferCanvas, viewport, variant.outputScale);

    await page.render({
      canvasContext: context,
      transform,
      viewport,
    }).promise;

    const textContent = await page.getTextContent({
      disableNormalization: true,
      includeMarkedContent: true,
    });

    page.cleanup();
    renderedPages.push({
      bufferCanvas,
      outputScale: variant.outputScale,
      pageNumber,
      textContent,
      viewport,
    });
  }

  return {
    ...variant,
    renderedPages,
  };
};

export const getVariantRecord = (root, variant) => {
  const cache = getRenderCache(root);
  let record = cache.get(variant.key);

  if (record) {
    return record;
  }

  record = {
    status: "pending",
    promise: Promise.resolve(),
    value: null,
  };
  record.promise = renderVariant(variant)
    .then((value) => {
      record.status = "fulfilled";
      record.value = value;
      return value;
    })
    .catch((error) => {
      cache.delete(variant.key);
      throw error;
    });

  cache.set(variant.key, record);
  return record;
};

export const drawVariantIntoLayer = async (pagesElement, renderedVariant) => {
  const pdfjs = await loadPdfJs();
  if (typeof pdfjs.renderTextLayer !== "function") {
    throw new Error("PDF.js renderTextLayer API is unavailable.");
  }

  const slots = getPageSlots(pagesElement);

  for (const [index, pageSlot] of slots.entries()) {
    const renderedPage = renderedVariant.renderedPages[index];
    if (!renderedPage) {
      clearPageSlot(pageSlot);
      continue;
    }

    pageSlot.slot.style.width = `${Math.floor(renderedPage.viewport.width)}px`;
    pageSlot.slot.style.height = `${Math.floor(renderedPage.viewport.height)}px`;
    paintPage(pageSlot.canvas, renderedPage, renderedVariant.pdfTitle);
    await renderTextLayer(pdfjs, pageSlot.textLayer, renderedPage);
    setHidden(pageSlot.slot, false);
  }
};
