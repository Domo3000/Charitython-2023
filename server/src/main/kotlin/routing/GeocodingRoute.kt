package routing

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.Location

private fun url(query: String) =
    "https://nominatim.openstreetmap.org/${query}format=json&limit=1&addressdetails=1"

private fun coordinatesUrl(street: String, zipCode: String) = url("search/$street,%20$zipCode?")
private fun addressUrl(latitude: String, longitude: String) = url("reverse?lat=$latitude&lon=$longitude&")

@Serializable
private data class Address(
    val house_number: String? = null,
    val road: String,
    val postcode: String
)

@Serializable
private data class CoordinateResponse(
    val lat: String,
    val lon: String,
    val address: Address
) {
    fun toDTO() = Location(lat, lon, address.road + (address.house_number?.let { " $it" } ?: ""), address.postcode)
}

private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

private suspend fun PipelineContext<Unit, ApplicationCall>.getLocation(url: String, readAsList: Boolean) {
    val client = HttpClient(CIO)
    val response = client.get(url) {
        userAgent(call.request.headers[HttpHeaders.UserAgent]!!)
    }

    val body = response.bodyAsText()

    val dto = if(readAsList) {
        val list = json.decodeFromString<List<CoordinateResponse>>(body)
        list.first().toDTO()
    } else {
        json.decodeFromString<CoordinateResponse>(body).toDTO()
    }

    client.close().also {
        call.respondMessage(dto)
    }
}

fun Route.geocodingRoute() = route("/data") {
    rateLimit {
        get("/coordinates/{zipCode}/{street}") {
            val zipCode = call.parameters["zipCode"]!!
            val street = call.parameters["street"]!!
                .replace(" ", "%20")

            getLocation(coordinatesUrl(street, zipCode), true)
        }
        get("/address/{latitude}/{longitude}") {
            val latitude = call.parameters["latitude"]!!
            val longitude = call.parameters["longitude"]!!

            getLocation(addressUrl(latitude, longitude), false)
        }
    }
}