package routing

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime
import model.*
import utils.saveImage
import utils.toLocalDateTime

fun Route.adminRoute() = authenticate(BASIC_AUTH) {
    route("/admin") {
        get("/login") {
            call.respond("Authenticated!")
        }
        route("/data") {

            post("/approveEvent/{id}") {
                val eventId = call.parameters["id"]!!.toInt()
                CleanupEventDao.approve(eventId)
                call.respond(HttpStatusCode.OK)
            }

            delete("/deleteEvent/{id}") {
                val eventId = call.parameters["id"]!!.toInt()
                CleanupEventDao.delete(eventId)
                call.respond(HttpStatusCode.OK)
            }

            post("/cleanupDay") {
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

                if (fileName == null) {
                    call.respond(HttpStatusCode.BadRequest, "file was not a supported image")
                } else {
                    CleanupDayDao.insert(date!!, fileName!!)

                    call.respondMessage(CleanupDayDao.getNext()?.toDTO())
                }
            }

            delete("/cleanupDay/{id}") {
                val id = Integer.parseInt(call.parameters["id"])

                if (CleanupDayDao.deleteById(id) == 1) {
                    call.respondMessage(DeletedCleanupDay)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}