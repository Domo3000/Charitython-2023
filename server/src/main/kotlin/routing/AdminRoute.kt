package routing

import com.sksamuel.scrimage.format.Format
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.*
import utils.Excel
import utils.saveImage
import utils.toLocalDateTime

fun Route.adminRoute() = authenticate(BASIC_AUTH) {
    route("/admin") {
        get("/login") {
            call.respond("Authenticated!")
        }
        route("/data") {
            get("/cleanupEvents") {
                val events = CleanupEventDao.getAll().map { it.toDTO() }
                call.respondMessage(CleanUpEvents(events))
            }

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
                            fileName = partData.saveImage(format = Format.PNG)
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

            post("/background") {
                val date: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                var fileName: String? = null

                call.receiveMultipart().forEachPart { partData ->
                    when (partData) {
                        is PartData.FileItem -> {
                            fileName = partData.saveImage(scaleToWidth = null)
                        }

                        else -> {}
                    }
                }

                if (fileName == null) {
                    call.respond(HttpStatusCode.BadRequest, "file was not a supported image")
                } else {
                    BackgroundDao.insert(date, fileName!!)

                    call.respondMessage(BackgroundDao.getLatest()?.toDTO())
                }
            }

            get("/cleanupEventResults/{id}") {
                val id = Integer.parseInt(call.parameters["id"])

                val results = CleanupEventResultDao.getAllByCleanupDayId(id)
                call.respondMessage(CleanupEventResultsDTO(results.map { it.toDTO() }))
            }

            get("/cleanupEventResults/{id}/excel") {
                val id = Integer.parseInt(call.parameters["id"])

                CleanupDayDao.getById(id)?.let { cleanupDay ->
                    val date = cleanupDay.date.toString()
                    val results = CleanupEventResultDao.getAllByCleanupDayId(id)

                    val file = Excel.createExcelFile(results, date)

                    call.respondFile(file)
                } ?: run {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            post("/cleanupEventResults/{id}") {
                val id = Integer.parseInt(call.parameters["id"])

                CleanupEventResultDao.markAsExported(id)
            }

            delete("/cleanupEventResults/{id}") {
                val id = Integer.parseInt(call.parameters["id"])

                CleanupEventResultDao.deleteById(id)

                call.respond(HttpStatusCode.OK)
            }

            delete("/cleanupEventResults") {
                CleanupEventResultDao.transformToCleanupDayResult()
                CleanupEventResultDao.deleteAll()

                call.respond(HttpStatusCode.OK)
            }

            post("/previousCleanupDayResults") {
                    val result = call.receive<CleanupDayResultsDTO>()
                    CleanupDayResultDao.deleteByCleanupDayId(result.cleanupDayId)
                    CleanupDayResultDao.insert(result.cleanupDayId, result.garbage, result.participants)
                    call.respond(HttpStatusCode.OK)
            }
        }
    }
}