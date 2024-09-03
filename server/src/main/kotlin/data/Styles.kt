package data

import kotlinx.css.*
import utils.Style

typealias CSS = CssBuilder.() -> Unit

fun CssBuilder.styles() {
    fontFace {
        fontFamily = "Poppins"
        src = "url('/static/fonts/Poppins/Poppins-Regular.ttf')"
    }

    fontFace {
        fontFamily = "Chau Philomene One"
        src = "url('/static/fonts/ChauPhilomeneOne/ChauPhilomeneOne-Regular.ttf')"
    }

    rule("*") {
        color = Color.white
        fontFamily = "Poppins, Sans-serif"
        textAlign = TextAlign.left
    }

    rule(".noBorder") {
        border = "0 !important"
    }

    body {
        backgroundColor = Color(Style.backgroundColor())
    }

    h1 {
        fontFamily = "Chau Philomene One, Sans-serif"
    }

    h2 {
        fontFamily = "Chau Philomene One, Sans-serif"
    }

    h3 {
        fontFamily = "Chau Philomene One, Sans-serif"
    }

    button {
        color = Color.black
        textAlign = TextAlign.center
        fontWeight = FontWeight.bold
    }

    input {
        color = Color.black
    }

    textarea {
        color = Color.black
    }

    span {
        color = Color.black
    }

    rule("#script-holder") {
        width = LinearDimension("100%")
        margin(0.px)
    }

    rule(".menu") {
        textAlign = TextAlign.left
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