import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.serialization.json.Json
import model.AdminDao
import model.CleanupDayDao
import model.CleanupDayResultDao
import org.slf4j.LoggerFactory
import routing.BASIC_AUTH
import routing.installRouting
import utils.Connection
import utils.logError
import utils.logInfo
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

private fun generatePassword(length: Int = 10): String {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val salt = Integer.valueOf(System.getenv("RANDOM_SALT") ?: "666")
    val random = Random(System.currentTimeMillis() / salt)
    return (1..10).map {
        random.nextInt(0, charPool.size).let { charPool[it] }
    }.joinToString("")
}

private fun readBooleanVariable(name: String) = try {
    System.getenv(name) == "true"
} catch (_: Exception) {
    false
}

private fun Application.body(debug: Boolean) {
    with(environment) {
        val database = if (debug) {
            Connection("jdbc:h2:file:./build/db", "user", "password")
        } else {
            val url = System.getenv("DB_URL")
            val user = System.getenv("DB_USER")
            val password = System.getenv("DB_PASSWORD")
            Connection(url, user, password)
        }

        val password: String = AdminDao.getLatest()?.let {
            if (readBooleanVariable("RESET_PASSWORD")) {
                return@let null
            } else {
                logInfo("current password: ${it.password}")
                return@let it.password
            }
        } ?: run {
            val newPassword = if (debug) {
                "admin"
            } else {
                generatePassword()
            }
            AdminDao.insert(newPassword)
            logInfo("generated new password: $newPassword")
            newPassword
        }

        CleanupDayDao.getLast() ?: run {
            logInfo("inserting default values")
            val previousCleanupDay = CleanupDayDao.insert(
                LocalDateTime(2022, Month.SEPTEMBER, 17, 0, 0, 0),
                "unused"
            )

            CleanupDayResultDao.insert(previousCleanupDay.id.value, 27.0, 14000)
        }

        authentication {
            basic(BASIC_AUTH) {
                realm = "/admin"
                validate { credentials ->
                    if (credentials.name == "admin" && credentials.password == password) {
                        UserIdPrincipal(credentials.name)
                    } else {
                        null
                    }
                }
            }
        }

        install(StatusPages) {
            exception<Throwable> { call, cause ->
                logError(cause)
                call.respondText(text = "500: $cause.", status = HttpStatusCode.InternalServerError)
            }
            status(HttpStatusCode.NotFound) { call, code ->
                call.respondText(text = "Page Not Found", status = code)
            }
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                classDiscriminator = "class"
            })
        }

        install(RateLimit) {
            register {
                rateLimiter(1, 1.seconds)
            }
        }

        install(CachingHeaders) {
            options { _, outgoingContent ->
                when (outgoingContent.contentType?.withoutParameters()) {
                    ContentType.Text.CSS, ContentType.Text.Html, ContentType.Image.JPEG, ContentType.Image.PNG ->
                        CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                    else -> null
                }
            }
        }

        installRouting()
    }
}

suspend fun main(): Unit = coroutineScope {
    val debug = readBooleanVariable("DEBUG")

    val environment = if (debug) {
        applicationEngineEnvironment {
            log = LoggerFactory.getLogger("ktor.application")
            connector {
                port = 8080
            }
            module {
                body(debug)
            }
        }
    } else {
        applicationEngineEnvironment {
            log = LoggerFactory.getLogger("ktor.application")
            connector {
                port = 8080
            }
            connector {
                port = 8081
            }
            module {
                body(debug)
            }
        }
    }

    embeddedServer(Netty, environment).start(wait = true)
}
