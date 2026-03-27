import {
  buildVariant,
  drawVariantIntoLayer,
  getVariantRecord,
} from "./cv-pdf-renderer.mjs";

const THEME_CHANGED_EVENT = "noahruben:theme-changed";
const CV_LANGUAGE_LINK_SELECTOR = "a[data-cv-language]";
const CV_COMMAND_SELECTOR = '[data-role="cv-command"]';
const CV_COMMAND_TEXT_SELECTOR = '[data-role="cv-command-text"]';
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

const updateCvCommand = (selectedLanguage) => {
  document.querySelectorAll(CV_COMMAND_SELECTOR).forEach((element) => {
    const commandText = element.querySelector(CV_COMMAND_TEXT_SELECTOR);

    if (commandText) {
      commandText.textContent = `noahruben cv ${selectedLanguage}`;
      return;
    }

    element.textContent = `>> noahruben cv ${selectedLanguage}`;
  });
};

const syncPendingVariant = (controller, variant) => {
  controller.root.setAttribute("data-current-language", variant.language);
  controller.root.setAttribute("data-pdf-url-base", variant.baseUrl);
  controller.root.setAttribute("data-pdf-title", variant.pdfTitle);
  updateLanguageLinks(controller.root, variant.language);
  updateCvCommand(variant.language);
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
};

const showError = (controller, message) => {
  controller.root.setAttribute("data-viewer-ready", "false");
  setHidden(controller.activeLayer, true);
  setHidden(controller.loadingElement, true);
  if (controller.errorElement) {
    controller.errorElement.textContent = message;
  }
  setHidden(controller.errorElement, false);
};

const removeLayer = (layer) => {
  if (layer && layer.isConnected) {
    layer.remove();
  }
};

const restoreCommittedView = (controller, stagingLayer = null) => {
  removeLayer(stagingLayer);

  if (!controller.lastCommittedVariant) {
    return;
  }

  syncCommittedVariant(controller, controller.lastCommittedVariant);
  controller.root.setAttribute("data-viewer-ready", "true");
  setHidden(controller.errorElement, true);
  setHidden(controller.loadingElement, true);
  setHidden(controller.activeLayer, false);
};

const commitVariant = (controller, stagingLayer, renderedVariant) => {
  const previousActiveLayer = controller.activeLayer;

  stagingLayer.setAttribute("data-role", "pages-active");
  setHidden(stagingLayer, false);
  previousActiveLayer.remove();

  controller.activeLayer = stagingLayer;
  controller.lastCommittedVariant = renderedVariant;

  syncCommittedVariant(controller, renderedVariant);
  controller.root.setAttribute(
    "data-render-id",
    `${Number.parseInt(controller.root.getAttribute("data-render-id") ?? "0", 10) + 1}`,
  );
  controller.root.setAttribute("data-viewer-ready", "true");

  setHidden(controller.errorElement, true);
  setHidden(controller.loadingElement, true);
};

const createStagingLayer = (controller) => {
  const stagingLayer = controller.layerTemplate.cloneNode(true);
  stagingLayer.setAttribute("data-role", "pages-staging");
  setHidden(stagingLayer, true);
  controller.activeLayer.insertAdjacentElement("afterend", stagingLayer);
  return stagingLayer;
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
    restoreCommittedView(controller);
    return;
  }

  controller.requestId += 1;
  const currentRequestId = controller.requestId;
  syncPendingVariant(controller, variant);

  const record = getVariantRecord(controller.root, variant);
  if (record.status !== "fulfilled") {
    showLoading(controller);
  }

  let stagingLayer = null;

  try {
    const renderedVariant =
      record.status === "fulfilled" ? record.value : await record.promise;
    if (currentRequestId !== controller.requestId) {
      return;
    }

    stagingLayer = createStagingLayer(controller);
    await drawVariantIntoLayer(stagingLayer, renderedVariant);
    if (currentRequestId !== controller.requestId) {
      removeLayer(stagingLayer);
      return;
    }

    commitVariant(controller, stagingLayer, renderedVariant);
    warmPreferredVariants(controller);
  } catch (error) {
    removeLayer(stagingLayer);

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

  return {
    activeLayer,
    errorElement,
    lastCommittedVariant: null,
    layerTemplate: activeLayer.cloneNode(true),
    loadingElement,
    requestId: 0,
    root,
  };
};

const getViewerState = () => {
  if (!window.__noahrubenCvViewerState) {
    window.__noahrubenCvViewerState = {
      controllerByRoot: new Map(),
      listenersRegistered: false,
    };
  }

  return window.__noahrubenCvViewerState;
};

const getViewerControllers = () => {
  const state = getViewerState();

  state.controllerByRoot.forEach((_, root) => {
    if (!root.isConnected) {
      state.controllerByRoot.delete(root);
    }
  });

  return Array.from(document.querySelectorAll("#cv-pdf-viewer"))
    .map((root) => {
      const existingController = state.controllerByRoot.get(root);
      if (existingController) {
        return existingController;
      }

      const controller = createController(root);
      if (!controller) {
        return null;
      }

      state.controllerByRoot.set(root, controller);

      const initialLanguage =
        controller.root.getAttribute("data-current-language") ??
        getAvailableLanguages()[0];
      updateLanguageLinks(controller.root, initialLanguage);
      void requestVariant(
        controller,
        buildVariantFor(controller, initialLanguage, getActiveMode()),
      );

      return controller;
    })
    .filter(Boolean);
};

getViewerControllers();

if (!getViewerState().listenersRegistered) {
  getViewerState().listenersRegistered = true;

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

    getViewerControllers().forEach((controller) => {
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

    getViewerControllers().forEach((controller) => {
      const language =
        controller.root.getAttribute("data-current-language") ??
        controller.lastCommittedVariant?.language ??
        getAvailableLanguages()[0];
      void requestVariant(controller, buildVariantFor(controller, language, mode));
    });
  });

  window.addEventListener("popstate", () => {
    const viewerControllers = getViewerControllers();
    if (viewerControllers.length === 0) {
      return;
    }

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

  document.addEventListener("htmx:afterSwap", () => {
    getViewerControllers();
  });
}
