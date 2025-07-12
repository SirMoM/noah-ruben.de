package de.noah_ruben.misc

object CssClasses {
    const val PAGE_TITLE = "page-title"
    const val MB_8 = "mb-8"
    const val MB_4 = "mb-4"
    const val CONTENT_CONTAINER = "content-container"

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
        const val PROJECT_CARD_CONTENT = "project-card-content"
        const val PROJECT_CARD_TITLE = "project-card-title"
        const val PROJECT_CARD_DESCRIPTION = "project-card-description"
        const val PROJECT_CARD_META = "project-card-meta"
        const val META_DETAIL_ROW = "meta-detail-row"
        const val META_DETAIL_LABEL = "meta-detail-label"
        const val TAGS_LIST = "tags-list"
        const val TAG_ITEM = "tag-item"
        const val PROJECT_CARD_FOOTER = "project-card-footer"
        const val PROJECT_ACTION_LINK = "project-action-link"
        const val RESET_BUTTON = "reset-button"
    }

    object Form {
        const val FORM_GROUP = "form-group"
        const val FORM_FIELD = "form-field"
        const val FORM_LABEL = "form-label"
        const val FORM_INPUT_BASE = "form-input-base"
        const val FORM_INPUT_TEXT = "form-input-text"
        const val FORM_CHECKBOX_GROUP = "form-checkbox-group"
        const val FORM_CHECKBOX = "form-checkbox"
        const val SUBMIT_BUTTON = "submit-button"
        const val LOADING_SPINNER = "loading-spinner"
        const val FILTER_CONTROLS_LAYOUT = "filter-controls-layout"
        const val FILTER_ITEM_LAYOUT = "filter-item-layout"
    }
}
