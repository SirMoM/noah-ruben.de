package de.noah_ruben.misc

import kotlinx.html.FlowContent

const val HX_POST = "hx-post"
const val HX_TARGET = "hx-target"
const val HX_SWAP = "hx-swap"
const val HX_TRIGGER = "hx-trigger"
const val HX_INDICATOR = "hx-indicator"

fun FlowContent.hxPost(url: String) {
    this.attributes[HX_POST] = url
}

fun FlowContent.hxTarget(target: String) {
    this.attributes[HX_TARGET] = target
}

fun FlowContent.hxSwap(swap: String) {
    this.attributes[HX_SWAP] = swap
}

fun FlowContent.hxTrigger(trigger: String) {
    this.attributes[HX_TRIGGER] = trigger
}
fun FlowContent.hxIndicator(indicator: String) {
    this.attributes[HX_INDICATOR] = indicator
}
