package utils

import data.CSS
import data.index
import data.styles
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.CssBuilder
import kotlinx.datetime.*
import kotlinx.html.HTML
import model.CleanupDay
import model.CleanupDayDTO
import model.CleanupDayDao

const val BASIC_AUTH = "auth-basic"

private suspend fun ApplicationCall.respondText(text: String) = respondText(text = text, status = HttpStatusCode.OK)

/**
 * https://ktor.io/docs/css-dsl.html#serve_css
 */
private suspend inline fun ApplicationCall.respondCss(css: CSS) {
    this.respondText(CssBuilder().apply(css).toString(), ContentType.Text.CSS)
}

fun Application.installRouting() = routing {
    authenticate(BASIC_AUTH) {
        route("/admin/") {
            get("/login") {
                call.respond("Authenticated!")
            }
            route("/data") {
                post("/cleanupDay") {
                    val new = call.receive<CleanupDayDTO>().timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                    CleanupDayDao.insert(new)
                }
            }
        }
    }

    route("/data") {
        get("/cleanupDay") {
            CleanupDayDao.getNext()?.let {
                call.respond(it)
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "current one might already be in past. next one is not set")
            }
        }
    }

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
    get("/{...}") {
        call.respondHtml(HttpStatusCode.OK, HTML::index)
    }
    static("/static") {
        resources("assets")
    }
}