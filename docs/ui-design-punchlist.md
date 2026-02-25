---
status: doing
created: 2026-02-19
completed:
---

# UI Design Punchlist

| Order | Priority | Issue | Why it matters | Primary targets | Done |
|---:|:---:|---|---|---|:---:|
| 1 | P0 | Broken CV navigation (`/cv` 404 + CLI `cv` exception) | Breaks a key CTA and trust immediately | `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:116`, `src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt:59`, `src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt:130`, `src/main/kotlin/de/noah_ruben/Application.kt:45` | No |
| 2 | P0 | Placeholder text in production UI (`TODO`, `: link`) | Feels unfinished and lowers perceived quality | `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:62`, `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:66`, `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:70`, `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:74`, `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:109`, `src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt:120` | No |
| 3 | P0 | Duplicate social row (`Twitter` listed twice) | Creates obvious content bug in hero/profile block | `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:62` | No |
| 4 | P1 | Inconsistent label/casing (`GitHub`/`Github`/`GITHUB`) | Reduces polish and UI consistency | `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:55`, `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:66`, `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:138`, `src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt:148` | No |
| 5 | P1 | Search button/loading copy collision (`Search Searching...`) | Makes interaction state confusing | `src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt:311`, `src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt:316` | No |
| 6 | P1 | Project list hard to scan (very long, repetitive cards) | Hurts browseability and content discovery | `src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt:74`, `src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt:85` | No |
| 7 | P1 | Mobile filter ergonomics need tightening | Improves usability on narrow screens | `src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt:226` | No |
| 8 | P2 | Landing hero hierarchy refinement | Better first-impression clarity while keeping terminal style | `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:34`, `src/main/kotlin/de/noah_ruben/site/LandingPage.kt:87` | No |
| 9 | P2 | Legacy/unused style cleanup | Lowers CSS drift and maintenance overhead | `tailwind/style.css` (line refs stale — file restructured in catppuccin migration) | No |
| 10 | P2 | Empty project description fallback | Prevents "broken card" appearance for empty repo descriptions | `src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt:102` | No |
