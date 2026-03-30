package de.noah_ruben.site

import de.noah_ruben.misc.CssClasses.Form.TOGGLE_BUTTON
import de.noah_ruben.misc.CssClasses.Form.TOGGLE_BUTTON_ICON_MOON_FULL
import de.noah_ruben.misc.CssClasses.Form.TOGGLE_BUTTON_ICON_SUN_FULL
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HEAD
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.id
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.title
import kotlinx.html.unsafe

fun DIV.selfLink(url: String, text: String) {
    a(href = url) {
        +" $text"
    }
}

fun DIV.githubLink() {
    a(href = "https://github.com/SirMoM") {
        +"GITHUB"
    }
}

fun HEAD.defaultHeader() {
    link(rel = "stylesheet", href = "/resources/style.css")
    link(rel = "icon", type = "image/png", href = "/resources/favicon-16x16.png") {
        sizes = "16x16"
    }
    link(rel = "icon", type = "image/png", href = "/resources/favicon-32x32.png") {
        sizes = "32x32"
    }
    link(rel = "apple-touch-icon", href = "/resources/apple-touch-icon.png") {
        sizes = "180x180"
    }
    link(rel = "icon", type = "image/png", href = "/resources/android-chrome-192x192.png") {
        sizes = "192x192"
    }
    script { src = "/resources/debug.js" }
    meta(charset = "UTF-8")
    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
    title("Noah Ruben")
    script {
        unsafe {
            +"""
                (function () {
                  function applyTheme() {
                    var savedTheme = localStorage.theme;
                    var prefersDark = !("theme" in localStorage) && window.matchMedia("(prefers-color-scheme: dark)").matches;
                    var theme = savedTheme || (prefersDark ? "mocha" : "latte");
                    var root = document.documentElement;
                    var currentTheme = root.classList.contains("mocha") ? "mocha" : root.classList.contains("latte") ? "latte" : null;
                    if (currentTheme !== theme) {
                      root.classList.remove("latte", "mocha");
                      root.classList.add(theme);
                      window.dispatchEvent(new CustomEvent("noahruben:theme-changed", {
                        detail: {
                          mode: theme === "mocha" ? "dark" : "light",
                        },
                      }));
                    }

                    var button = document.getElementById("theme-toggle");
                    if (!button) return;
                    var isDark = theme === "mocha";
                    button.setAttribute("aria-pressed", String(isDark));
                    button.setAttribute("data-theme", isDark ? "dark" : "light");
                    button.querySelectorAll("span").forEach(function (icon) {
                      icon.setAttribute("data-theme", isDark ? "dark" : "light");
                    });
                  }

                  applyTheme();
                  document.addEventListener("DOMContentLoaded", applyTheme);

                  document.addEventListener("click", function (event) {
                    var target = event.target;
                    if (!(target instanceof Element)) return;
                    if (!target.closest("#theme-toggle")) return;

                    var isDark = document.documentElement.classList.contains("mocha");
                    localStorage.theme = isDark ? "latte" : "mocha";

                    applyTheme();
                  });

                  var applyAccent = function(hex) { document.documentElement.style.setProperty('--accent', hex); };
                  window.setAccentColor = function(hex) { localStorage.setItem('accent', hex); applyAccent(hex); };
                  var savedAccent = localStorage.getItem('accent');
                  if (savedAccent) applyAccent(savedAccent);
                })();
            """.trimIndent()
        }
    }
    script {
        src = "https://unpkg.com/htmx.org@1.9.11"
        integrity = "sha384-0gxUXCCR8yv9FM2b+U3FDbsKthCI66oH5IA9fHppQq9DDMHuMauqq1ZHBpJxQ0J0"
        attributes["crossorigin"] = "anonymous"
    }
    // script(src = "https://cdn.tailwindcss.com") {}
    link(rel = "stylesheet", href = "https://fonts.cdnfonts.com/css/cascadia-code")
}

fun FlowContent.themeToggleButton() {
    button(classes = TOGGLE_BUTTON) {
        id = "theme-toggle"
        attributes["type"] = "button"
        attributes["aria-label"] = "Toggle dark mode"
        attributes["aria-pressed"] = "false"
        attributes["data-theme"] = "light"
        span(classes = TOGGLE_BUTTON_ICON_MOON_FULL) {
            unsafe {
                +"""
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                      <use href="/resources/icons/theme-icons.svg#moon"></use>
                    </svg>
                """.trimIndent()
            }
        }
        span(classes = TOGGLE_BUTTON_ICON_SUN_FULL) {
            unsafe {
                +"""
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                      <use href="/resources/icons/theme-icons.svg#sun"></use>
                    </svg>
                """.trimIndent()
            }
        }
    }
}
