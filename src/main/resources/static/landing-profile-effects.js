/**
 * @typedef {{ nextEffectName: string | null }} LandingEffectsTestHooks
 */

(() => {
    const BASE_ALT_TEXT = "Portrait of Noah Ruben";
    const EFFECTS = ["greyscale", "nearest neighbour", "bilinear", "text", "ascii"];
    const WASM_EXEC_SRC = "/resources/gasm-effects/wasm_exec.js";
    const WASM_MODULE_SRC = "/resources/gasm-effects/test.wasm";
    const state = {
        busy: false,
        bootId: 0,
        runtime: null,
        originalImage: null,
        originalSrc: null,
    };

    /** @type {LandingEffectsTestHooks} */
    const testHooks = window.__noahrubenLandingEffectsTest ?? { nextEffectName: null };
    window.__noahrubenLandingEffectsTest = testHooks;

    function resetRuntime(bootId) {
        if (bootId && state.bootId !== bootId) {
            return;
        }

        state.runtime = null;
        window.goMI = undefined;
    }

    /**
     * Loads and boots the Go WASM runtime once, then waits for `goMI` to become available.
     *
     * @returns {Promise<void>}
     */
    async function ensureRuntime() {
        if (typeof window.goMI === "function") {
            return;
        }

        if (!state.runtime) {
            state.runtime = (async () => {
                const bootId = ++state.bootId;
                if (!window.Go) {
                    await new Promise((resolve, reject) => {
                        const existing = document.querySelector(`script[src="${WASM_EXEC_SRC}"]`);
                        if (existing instanceof HTMLScriptElement) {
                            existing.addEventListener("load", resolve, { once: true });
                            existing.addEventListener("error", () => reject(new Error(`Failed to load ${WASM_EXEC_SRC}`)), { once: true });
                            return;
                        }

                        const script = document.createElement("script");
                        script.src = WASM_EXEC_SRC;
                        script.addEventListener("load", resolve, { once: true });
                        script.addEventListener("error", () => reject(new Error(`Failed to load ${WASM_EXEC_SRC}`)), { once: true });
                        document.head.append(script);
                    });
                }

                const go = new window.Go();
                const wasm = await WebAssembly.instantiateStreaming(fetch(WASM_MODULE_SRC), go.importObject);
                Promise.resolve(go.run(wasm.instance))
                    .catch((error) => {
                        console.error("[landing-profile-effects] Go runtime exited unexpectedly.", error);
                    })
                    .finally(() => {
                        resetRuntime(bootId);
                    });

                await new Promise((resolve, reject) => {
                    let attempts = 0;
                    const poll = () => {
                        if (typeof window.goMI === "function") {
                            resolve();
                            return;
                        }
                        attempts += 1;
                        if (attempts > 200) {
                            reject(new Error("goMI did not become available after WASM initialization."));
                            return;
                        }
                        window.setTimeout(poll, 10);
                    };
                    poll();
                });
            })().catch((error) => {
                resetRuntime();
                throw error;
            });
        }

        await state.runtime;
    }

    function getOriginalImage(img) {
        const src = img.getAttribute("data-original-src");
        if (!src) {
            return Promise.reject(new Error("Landing profile image is missing data-original-src."));
        }

        if (state.originalImage && state.originalSrc === src) {
            return state.originalImage;
        }

        state.originalSrc = src;
        state.originalImage = new Promise((resolve, reject) => {
            const original = new Image();
            original.src = src;
            original.addEventListener("load", () => resolve(original), { once: true });
            original.addEventListener("error", () => reject(new Error(`Failed to decode original image: ${src}`)), { once: true });
        });
        return state.originalImage;
    }

    /**
     * Builds an `ImageData` payload from the untouched original image using the same cover crop
     * the visible profile image uses on the page.
     *
     * @param {HTMLImageElement} img
     * @param {HTMLImageElement} original
     * @returns {{ canvas: HTMLCanvasElement, context: CanvasRenderingContext2D }}
     */
    function renderOriginal(img, original) {
        const width = Math.max(1, Math.round(img.clientWidth || img.width || original.naturalWidth));
        const height = Math.max(1, Math.round(img.clientHeight || img.height || original.naturalHeight));
        const canvas = document.createElement("canvas");
        const context = canvas.getContext("2d");
        if (!context) {
            throw new Error("Could not create a 2D canvas context.");
        }

        canvas.width = width;
        canvas.height = height;

        const targetRatio = width / height;
        const sourceRatio = original.naturalWidth / original.naturalHeight;
        const sourceWidth = sourceRatio > targetRatio
            ? Math.round(original.naturalHeight * targetRatio)
            : original.naturalWidth;
        const sourceHeight = sourceRatio > targetRatio
            ? original.naturalHeight
            : Math.round(original.naturalWidth / targetRatio);
        const sourceX = Math.round((original.naturalWidth - sourceWidth) / 2);
        const sourceY = Math.round((original.naturalHeight - sourceHeight) / 2);

        context.drawImage(original, sourceX, sourceY, sourceWidth, sourceHeight, 0, 0, width, height);
        return { canvas, context };
    }

    /**
     * Runs one image-manipulation effect against the landing profile image.
     *
     * @param {HTMLImageElement} imgElement
     * @returns {void}
     */
    window.runLandingProfileEffect = (imgElement) => {
        if (!(imgElement instanceof HTMLImageElement) || state.busy) {
            return;
        }

        state.busy = true;
        const effect = EFFECTS.includes(testHooks.nextEffectName ?? "")
            ? testHooks.nextEffectName
            : EFFECTS[Math.floor(Math.random() * EFFECTS.length)];
        testHooks.nextEffectName = null;

        void (async () => {
            try {
                await ensureRuntime();
                const original = await getOriginalImage(imgElement);
                const { context } = renderOriginal(imgElement, original);
                const input = context.getImageData(0, 0, context.canvas.width, context.canvas.height);
                const output = window.goMI(effect, input);

                if (!output || typeof output.width !== "number" || typeof output.height !== "number" || !output.data) {
                    throw new Error(`goMI returned an invalid payload for effect "${effect}".`);
                }

                const canvas = document.createElement("canvas");
                const outContext = canvas.getContext("2d");
                if (!outContext) {
                    throw new Error("Could not create a 2D canvas context.");
                }

                canvas.width = output.width;
                canvas.height = output.height;
                outContext.putImageData(
                    new ImageData(Uint8ClampedArray.from(output.data), output.width, output.height),
                    0,
                    0,
                );
                imgElement.src = canvas.toDataURL("image/png");
                imgElement.alt = `${BASE_ALT_TEXT} with ${effect} effect applied`;
            } catch (error) {
                resetRuntime();
                console.error("[landing-profile-effects] Failed to manipulate landing portrait.", error);
                const source = imgElement.getAttribute("data-original-src");
                if (source) {
                    imgElement.src = source;
                }
                imgElement.alt = BASE_ALT_TEXT;
            } finally {
                state.busy = false;
            }
        })();
    };
})();
