package data

import kotlinx.html.*

fun HTML.index() {
    head {
        title("Green Heroes")
        meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1.0"
        }
        link {
            rel = "stylesheet"
            href = "/static/styles.css"
        }
    }
    body {
        div {
            id = "script-holder"
            script(src = "/static/main.js") { }
        }
    }
}