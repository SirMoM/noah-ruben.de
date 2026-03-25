const PDF_JS_VERSION = "4.0.379";
const MAX_PAGE_COUNT = 3;
const PDF_JS_MODULE_URL =
  `https://cdn.jsdelivr.net/npm/pdfjs-dist@${PDF_JS_VERSION}/build/pdf.min.mjs`;
const PDF_JS_WORKER_URL =
  `https://cdn.jsdelivr.net/npm/pdfjs-dist@${PDF_JS_VERSION}/build/pdf.worker.min.mjs`;

let pdfJsPromise = null;

const renderCaches = new WeakMap();

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
  canvas.style.display = "block";
  canvas.style.maxWidth = "100%";
};

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

    page.cleanup();
    renderedPages.push({
      bufferCanvas,
      outputScale: variant.outputScale,
      pageNumber,
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

export const drawVariantIntoLayer = (pagesElement, renderedVariant) => {
  getPageCanvases(pagesElement).forEach((canvas, index) => {
    const renderedPage = renderedVariant.renderedPages[index];
    if (!renderedPage) {
      canvas.setAttribute("hidden", "");
      canvas.removeAttribute("aria-label");
      return;
    }

    canvas.removeAttribute("hidden");
    paintPage(canvas, renderedPage, renderedVariant.pdfTitle);
  });
};
