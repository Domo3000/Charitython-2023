package routing

import data.CSS
import data.index
import data.styles
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.CssBuilder
import kotlinx.html.HTML
import model.Message
import java.io.File
import java.io.IOException

const val BASIC_AUTH = "auth-basic"

suspend fun ApplicationCall.respondText(text: String) = respondText(text = text, status = HttpStatusCode.OK)

/**
 * https://ktor.io/docs/css-dsl.html#serve_css
 */
suspend inline fun ApplicationCall.respondCss(css: CSS) {
    this.respondText(CssBuilder().apply(css).toString(), ContentType.Text.CSS)
}

suspend fun ApplicationCall.respondMessage(message: Message?) {
    message?.let { this.respond(it.encode()) } ?: this.respond("")
}

fun Application.installRouting() = routing {
    adminRoute()
    publicRoute()
    geocodingRoute()

    get("/health") {
        call.respondText("Healthy!")
    }
    get("/robots.txt") {
        call.respondText(
            """User-agent: *
            |Allow: /""".trimMargin()
        )
    }
    get("/static/styles.css") {
        call.respondCss(CssBuilder::styles)
    }
    get("/files/{fileName}") {
        val fileName = call.parameters["fileName"]
        val baseDirectory = "${System.getProperty("user.dir")}/files"

        try {
            call.respondFile(File("$baseDirectory/$fileName")) {
                caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 365 * 24 * 60 * 60))
            }
        } catch (e: IOException) {
            call.respond(HttpStatusCode.NotFound)
        }
    }
    get("/{...}") {
        call.respondHtml(HttpStatusCode.OK, HTML::index)
    }
    static("/static") {
        resources("assets")
    }
}