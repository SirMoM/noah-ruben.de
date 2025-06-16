package de.noah_ruben.misc

object CssClasses {
    const val PAGE_TITLE = "page-title"
    const val MB_8 = "mb-8"
    const val MB_4 = "mb-4"
    object LandingPage {
        const val PROFILE_CONTAINER = "nf-profile-container"
        const val PROFILE_PICTURE = "nf-profile-picture"
        const val PROFILE_DETAILS_CONTAINER = "nf-profile-details-container"
        const val COLOR_GRID = "color-grid"
        val COLORS = listOf(
            "bg-rose-500", "bg-red-500", "bg-green-500", "bg-purple-500",
            "bg-indigo-500", "bg-blue-500", "bg-cyan-500", "bg-teal-500",
            "bg-emerald-500", "bg-green-500", "bg-lime-500", "bg-yellow-500",
            "bg-orange-500", "bg-red-500", "bg-gray-500", "bg-black-500",
        )
        const val ABOUT_ME = "about-me-container"
    }

    object ProjectPage {
        const val PROJECT_CARD = "project-card"
    }
}
