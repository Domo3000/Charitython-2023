package routing

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.*
import utils.saveImage

fun Route.publicRoute() = route("/data") {
    get("/cleanupDay") {
        CleanupDayDao.getNext()?.let {
            call.respondMessage(it.toDTO())
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "current one might already be in past. next one is not set")
        }
    }
    get("/cleanupDay/{cleanupDayId}") {
        val cleanupDayId = Integer.parseInt(call.parameters["cleanupDayId"])

        CleanupDayDao.getById(cleanupDayId)?.let {
            call.respondMessage(it.toDTO())
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "current one might already be in past. next one is not set")
        }
    }
    get("/previousCleanupDay") {
        CleanupDayDao.getLast()?.let {
            call.respondMessage(it.toDTO())
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "no previous cleanupDay found")
        }
    }
    get("/previousCleanupDayResults") {
        val maybeResults = CleanupDayDao.getLast()?.let { previousCleanupDay ->
            CleanupDayResultDao.getByIdCleanupDayId(previousCleanupDay.id.value)?.toDTO(previousCleanupDay.date)
        }

        maybeResults?.let {
            call.respondMessage(it)
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "no results found")
        }
    }
    get("/background") {
        BackgroundDao.getLatest()?.let {
            call.respondMessage(it.toDTO())
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "current one might already be in past. next one is not set")
        }
    }
    get("/cleanupEvent/{eventId}") {
        val eventId = Integer.parseInt(call.parameters["eventId"])
        val event = CleanupEventDao.getById(eventId)
        event?.let {
            call.respondMessage(event.toDTO())
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "Did not find event for id $eventId")
        }
    }
    get("/cleanupEvents/{cleanupDayId}") {
        val cleanupDayId = Integer.parseInt(call.parameters["cleanupDayId"])
        val events = CleanupEventDao.getAllByCleanupDayId(cleanupDayId).map { it.toDTO() }
        call.respondMessage(CleanUpEvents(events))
    }
    post("/cleanupEvent") {
        val dto = call.receive<CleanUpEventCreationDTO>()
        val new = CleanupEventDao.insert(dto, null)

        call.respondMessage(IdMessage(new.id.value))
    }
    post("/cleanupEventWithPicture") {
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

        val new = CleanupEventDao.insert(dto!!, fileName)

        call.respondMessage(IdMessage(new.id.value))
    }
    post("/cleanupEventResult") {
        val result = call.receive<CleanupEventResultDTO>()
        CleanupEventResultDao.insert(result)
        call.respond(HttpStatusCode.OK)
    }
}