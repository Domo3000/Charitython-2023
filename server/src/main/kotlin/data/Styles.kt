package data

import kotlinx.css.*
import utils.Style

typealias CSS = CssBuilder.() -> Unit

fun CssBuilder.styles() {
    rule("*") {
        color = Color.black
        fontFamily = "Lucida Console, monospace"
        textAlign = TextAlign.left
    }

    body {
        backgroundColor = Color(Style.backgroundColor)
    }

    button {
        textAlign = TextAlign.center
        fontWeight = FontWeight.bold
        fontFamily = "Monaco, monospace"
    }

    rule("#script-holder") {
        width = LinearDimension("100%")
        margin(0.px)
    }

    rule(".menu") {
        textAlign = TextAlign.left
        fontFamily = "Monaco, monospace"
        minWidth = LinearDimension("200px")
    }

    media("only screen and (min-width: 800px)") {
        rule(".phone-element") {
            display = Display.none
        }
    }

    media("only screen and (max-width: 800px)") {
        rule(".desktop-element") {
            display = Display.none
        }
        rule("#content-holder") {
            width = LinearDimension("100%")
        }
    }

    media("only screen and (max-width: 600px)") {
        rule(".phone-full-width") {
            width = LinearDimension("100% !important")
        }
    }
}