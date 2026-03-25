import {
  buildVariant,
  drawVariantIntoLayer,
  getVariantRecord,
} from "./cv-pdf-renderer.mjs";

const THEME_CHANGED_EVENT = "noahruben:theme-changed";
const CV_LANGUAGE_LINK_SELECTOR = "a[data-cv-language]";
const PREFETCH_IDLE_TIMEOUT_MS = 1500;

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

const scheduleWhenIdle = (callback) => {
  if ("requestIdleCallback" in window) {
    window.requestIdleCallback(callback, {
      timeout: PREFETCH_IDLE_TIMEOUT_MS,
    });
    return;
  }

  window.setTimeout(callback, PREFETCH_IDLE_TIMEOUT_MS);
};

const getActiveMode = () =>
  document.documentElement.classList.contains("mocha") ? "dark" : "light";

const getAlternateMode = (mode) => (mode === "dark" ? "light" : "dark");

const getLanguageLinks = () =>
  Array.from(document.querySelectorAll(CV_LANGUAGE_LINK_SELECTOR)).filter(
    (link) => link instanceof HTMLAnchorElement,
  );

const getLanguageLink = (language) =>
  getLanguageLinks().find(
    (link) => link.getAttribute("data-cv-language") === language,
  ) ?? null;

const getAvailableLanguages = () => {
  const languages = getLanguageLinks()
    .map((link) => link.getAttribute("data-cv-language"))
    .filter(Boolean);
  return [...new Set(languages)];
};

const updateLanguageLinks = (root, selectedLanguage) => {
  const baseClass = root.getAttribute("data-toggle-link-base") ?? "";
  const activeClass = root.getAttribute("data-toggle-link-active") ?? "";
  const inactiveClass = root.getAttribute("data-toggle-link-inactive") ?? "";

  getLanguageLinks().forEach((link) => {
    const isSelected =
      link.getAttribute("data-cv-language") === selectedLanguage;
    link.setAttribute("data-selected", String(isSelected));
    link.className = `${baseClass} ${isSelected ? activeClass : inactiveClass}`.trim();
  });
};

const syncPendingVariant = (controller, variant) => {
  controller.root.setAttribute("data-current-language", variant.language);
  controller.root.setAttribute("data-pdf-url-base", variant.baseUrl);
  controller.root.setAttribute("data-pdf-title", variant.pdfTitle);
  updateLanguageLinks(controller.root, variant.language);
};

const syncCommittedVariant = (controller, variant) => {
  syncPendingVariant(controller, variant);
  controller.root.setAttribute("data-pdf-url", variant.pdfUrl);
};

const showLoading = (controller) => {
  controller.root.setAttribute("data-viewer-ready", "false");
  setHidden(controller.errorElement, true);
  setHidden(controller.loadingElement, false);
  setHidden(controller.activeLayer, true);
  setHidden(controller.stagingLayer, true);
};

const showError = (controller, message) => {
  controller.root.setAttribute("data-viewer-ready", "false");
  setHidden(controller.activeLayer, true);
  setHidden(controller.stagingLayer, true);
  setHidden(controller.loadingElement, true);
  if (controller.errorElement) {
    controller.errorElement.textContent = message;
  }
  setHidden(controller.errorElement, false);
};

const restoreCommittedView = (controller) => {
  if (!controller.lastCommittedVariant) {
    return;
  }

  syncCommittedVariant(controller, controller.lastCommittedVariant);
  controller.root.setAttribute("data-viewer-ready", "true");
  setHidden(controller.errorElement, true);
  setHidden(controller.loadingElement, true);
  setHidden(controller.activeLayer, false);
  setHidden(controller.stagingLayer, true);
};

const commitVariant = (controller, renderedVariant) => {
  drawVariantIntoLayer(controller.stagingLayer, renderedVariant);

  const nextActiveLayer = controller.stagingLayer;
  const nextStagingLayer = controller.activeLayer;
  nextActiveLayer.setAttribute("data-role", "pages-active");
  nextStagingLayer.setAttribute("data-role", "pages-staging");

  controller.activeLayer = nextActiveLayer;
  controller.stagingLayer = nextStagingLayer;
  controller.lastCommittedVariant = renderedVariant;

  syncCommittedVariant(controller, renderedVariant);
  controller.root.setAttribute(
    "data-render-id",
    `${Number.parseInt(controller.root.getAttribute("data-render-id") ?? "0", 10) + 1}`,
  );
  controller.root.setAttribute("data-viewer-ready", "true");

  setHidden(controller.errorElement, true);
  setHidden(controller.loadingElement, true);
  setHidden(controller.activeLayer, false);
  setHidden(controller.stagingLayer, true);
};

const buildVariantFor = (controller, language, mode) =>
  buildVariant(controller.root, getLanguageLink(language), mode);

const warmVariants = (controller, variants) => {
  variants.filter(Boolean).forEach((variant, index) => {
    const warm = () => {
      void getVariantRecord(controller.root, variant).promise.catch(() => {});
    };

    if (index === 0) {
      warm();
      return;
    }

    scheduleWhenIdle(warm);
  });
};

const warmPreferredVariants = (controller) => {
  const committed = controller.lastCommittedVariant;
  if (!committed) {
    return;
  }

  const otherLanguages = getAvailableLanguages().filter(
    (language) => language !== committed.language,
  );

  warmVariants(controller, [
    buildVariantFor(
      controller,
      committed.language,
      getAlternateMode(committed.mode),
    ),
    ...otherLanguages.map((language) =>
      buildVariantFor(controller, language, committed.mode),
    ),
    ...otherLanguages.map((language) =>
      buildVariantFor(controller, language, getAlternateMode(committed.mode)),
    ),
  ]);
};

const pushHistoryUrl = (pageUrl) => {
  const nextUrl = new URL(pageUrl, window.location.origin);
  const currentUrl = new URL(window.location.href);

  if (
    currentUrl.pathname === nextUrl.pathname &&
    currentUrl.search === nextUrl.search
  ) {
    return;
  }

  history.pushState(null, "", nextUrl);
};

const requestVariant = async (controller, variant, options = {}) => {
  if (!variant) {
    return;
  }

  if (options.pushHistory) {
    pushHistoryUrl(variant.pageUrl);
  }

  if (controller.lastCommittedVariant?.key === variant.key) {
    syncCommittedVariant(controller, variant);
    return;
  }

  controller.requestId += 1;
  const currentRequestId = controller.requestId;
  syncPendingVariant(controller, variant);

  const record = getVariantRecord(controller.root, variant);
  if (record.status !== "fulfilled") {
    showLoading(controller);
  }

  try {
    const renderedVariant =
      record.status === "fulfilled" ? record.value : await record.promise;
    if (currentRequestId !== controller.requestId) {
      return;
    }

    commitVariant(controller, renderedVariant);
    warmPreferredVariants(controller);
  } catch (error) {
    if (currentRequestId !== controller.requestId) {
      return;
    }

    console.error("[cv-pdf-viewer] Failed to render CV PDF preview.", error);

    if (controller.lastCommittedVariant) {
      restoreCommittedView(controller);
      return;
    }

    showError(controller, "The PDF preview is unavailable right now.");
  }
};

const createController = (root) => {
  const activeLayer = root.querySelector('[data-role="pages-active"]');
  const loadingElement = root.querySelector('[data-role="loading"]');
  const errorElement = root.querySelector('[data-role="error"]');

  if (!(activeLayer instanceof Element) || !(loadingElement instanceof Element)) {
    return null;
  }

  const stagingLayer = activeLayer.cloneNode(true);
  stagingLayer.setAttribute("data-role", "pages-staging");
  setHidden(stagingLayer, true);
  activeLayer.insertAdjacentElement("afterend", stagingLayer);

  return {
    activeLayer,
    errorElement,
    lastCommittedVariant: null,
    loadingElement,
    requestId: 0,
    root,
    stagingLayer,
  };
};

const viewerControllers = Array.from(document.querySelectorAll("#cv-pdf-viewer"))
  .map((root) => createController(root))
  .filter(Boolean);

viewerControllers.forEach((controller) => {
  const initialLanguage =
    controller.root.getAttribute("data-current-language") ??
    getAvailableLanguages()[0];
  updateLanguageLinks(controller.root, initialLanguage);
  void requestVariant(
    controller,
    buildVariantFor(controller, initialLanguage, getActiveMode()),
  );
});

if (viewerControllers.length > 0) {
  document.addEventListener("click", (event) => {
    if (event.defaultPrevented || !(event.target instanceof Element)) {
      return;
    }

    const link = event.target.closest(CV_LANGUAGE_LINK_SELECTOR);
    if (!(link instanceof HTMLAnchorElement)) {
      return;
    }

    if (
      event.button !== 0 ||
      event.metaKey ||
      event.ctrlKey ||
      event.shiftKey ||
      event.altKey
    ) {
      return;
    }

    event.preventDefault();
    const language = link.getAttribute("data-cv-language");
    const mode = getActiveMode();

    viewerControllers.forEach((controller) => {
      void requestVariant(
        controller,
        buildVariantFor(controller, language, mode),
        { pushHistory: true },
      );
    });
  });

  window.addEventListener(THEME_CHANGED_EVENT, (event) => {
    const mode =
      event instanceof CustomEvent && event.detail?.mode === "dark"
        ? "dark"
        : "light";

    viewerControllers.forEach((controller) => {
      const language =
        controller.root.getAttribute("data-current-language") ??
        controller.lastCommittedVariant?.language ??
        getAvailableLanguages()[0];
      void requestVariant(controller, buildVariantFor(controller, language, mode));
    });
  });

  window.addEventListener("popstate", () => {
    const url = new URL(window.location.href);
    const language =
      getLanguageLink(url.searchParams.get("lang"))?.getAttribute(
        "data-cv-language",
      ) ??
      viewerControllers[0].root.getAttribute("data-current-language") ??
      getAvailableLanguages()[0];
    const mode = getActiveMode();

    viewerControllers.forEach((controller) => {
      void requestVariant(controller, buildVariantFor(controller, language, mode));
    });
  });
}
