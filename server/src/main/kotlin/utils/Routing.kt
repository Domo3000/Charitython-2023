package utils

import data.CSS
import data.index
import data.styles
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.CssBuilder
import kotlinx.datetime.LocalDateTime
import kotlinx.html.HTML
import model.*
import java.io.File
import java.io.IOException

const val BASIC_AUTH = "auth-basic"

private suspend fun ApplicationCall.respondText(text: String) = respondText(text = text, status = HttpStatusCode.OK)

/**
 * https://ktor.io/docs/css-dsl.html#serve_css
 */
private suspend inline fun ApplicationCall.respondCss(css: CSS) {
    this.respondText(CssBuilder().apply(css).toString(), ContentType.Text.CSS)
}

private suspend fun ApplicationCall.respondMessage(message: Message?) {
    message?.let { this.respond(it.encode()) } ?: this.respond("")
}

fun Application.installRouting() = routing {
    authenticate(BASIC_AUTH) {
        route("/admin") {
            get("/login") {
                call.respond("Authenticated!")
            }
            route("/data") {
                post("/cleanupDay") {
                    // TODO: for normal requests use something like the following 3 lines
                    //val new = call.receive<CreateCleanupDay>().timestamp.toLocalDateTime()
                    //CleanupDayDao.insert(new)
                    //call.respondMessage(CleanupDayDao.getNext()?.toDTO())

                    var date: LocalDateTime? = null
                    var fileName: String? = null

                    call.receiveMultipart().forEachPart { partData ->
                        when (partData) {
                            is PartData.FormItem -> {
                                date = (Messages.decode(partData.value) as CreateCleanupDay).timestamp.toLocalDateTime()
                            }

                            is PartData.FileItem -> {
                                fileName = partData.saveImage()
                            }

                            else -> {}
                        }
                    }

                    if(fileName == null) {
                        call.respond(HttpStatusCode.BadRequest, "file was not a supported image")
                    } else {
                        CleanupDayDao.insert(date!!, fileName!!)

                        call.respondMessage(CleanupDayDao.getNext()?.toDTO())
                    }
                }

                delete("/cleanupDay/{id}") {
                    val id = Integer.parseInt(call.parameters["id"])

                    if(CleanupDayDao.deleteById(id) == 1) {
                        call.respondMessage(DeletedCleanupDay)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }

    route("/data") {
        get("/cleanupDay") {
            CleanupDayDao.getNext()?.let {
                call.respondMessage(it.toDTO())
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "current one might already be in past. next one is not set")
            }
        }
        get("/cleanupEvents") {
            val events = CleanupEventDao.getAll().map { it.toDTO() }
            call.respondMessage(CleanUpEvents(events))
        }
        post("/cleanupEvent") {
            var dto: CleanUpEventCreationDTO? = null
            var fileName: String? = null

            call.receiveMultipart().forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        dto = (Messages.decode(partData.value) as CleanUpEventCreationDTO)
                    }

                    is PartData.FileItem -> {
                        fileName = partData.saveImage()
                    }

                    else -> {}
                }
            }

            if(fileName == null) {
                call.respond(HttpStatusCode.BadRequest, "file was not a supported image")
            } else {
                val new = CleanupEventDao.insert(dto!!, fileName!!)

                call.respondMessage(IdMessage(new.id.value))
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
    get("/files/{fileName}") {
        val fileName = call.parameters["fileName"]
        val baseDirectory = "${System.getProperty("user.dir")}/files"

        try {
            call.respondFile(File("$baseDirectory/$fileName"))
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