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
        link {
            rel = "icon"
            type = "image/png"
            sizes = "16x16"
            href = "/static/favicon-16x16.png"
        }
        link {
            rel = "icon"
            type = "image/png"
            sizes = "32x32"
            href = "/static/favicon-32x32.png"
        }
        script {
            src = "https://kit.fontawesome.com/4110b65d96.js"
        }
    }
    body {
        div {
            id = "script-holder"
            script(src = "/static/main.js") { }
        }
    }
}