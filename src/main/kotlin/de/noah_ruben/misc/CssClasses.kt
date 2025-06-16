package de.noah_ruben.misc

object CssClasses {
    const val PAGE_TITLE = "page-title"
    object LandingPage {
        const val PROFILE_PICTURE = "profile-picture"
        const val COLOR_GRID = "color-grid"
        const val COLOR_SQUARE = "color-grid-square-base"
        val COLORS = listOf(
            "bg-rose-500", "bg-red-500", "bg-green-500", "bg-purple-500",
            "bg-indigo-500", "bg-blue-500", "bg-cyan-500", "bg-teal-500",
            "bg-emerald-500", "bg-green-500", "bg-lime-500", "bg-yellow-500",
            "bg-orange-500", "bg-red-500", "bg-gray-500", "bg-black-500"
        )
    }

    object ProjectPage {
        const val PROJECT_CARD = "project-card"
    }
}
