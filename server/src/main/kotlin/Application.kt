import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.serialization.json.Json
import model.AdminDao
import model.CleanupDayDao
import model.CleanupDayResultsDao
import org.slf4j.LoggerFactory
import routing.BASIC_AUTH
import routing.installRouting
import utils.Connection
import utils.logError
import utils.logInfo
import java.io.File
import java.security.KeyStore
import kotlin.random.Random

private fun getSystemCharArray(key: String) = System.getenv(key).toCharArray()

private fun generatePassword(length: Int = 10): String {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val salt = Integer.valueOf(System.getenv("RANDOM_SALT") ?: "666")
    val random = Random(System.currentTimeMillis() / salt)
    return (1..10).map {
        random.nextInt(0, charPool.size).let { charPool[it] }
    }.joinToString("")
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

        val password = if(debug) {
            "admin"
        } else {
            generatePassword()
        }

        AdminDao.insert(password)
        logInfo("generated new password: $password")

        CleanupDayDao.getLast() ?: run {
            logInfo("inserting default values")
            val previousCleanupDay = CleanupDayDao.insert(
                LocalDateTime(2022, Month.SEPTEMBER, 17, 0, 0, 0),
                "unused"
            )

            CleanupDayResultsDao.insert(previousCleanupDay.id.value, 27.0, 14000)
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

        installRouting()
    }
}

suspend fun main(): Unit = coroutineScope {
    val keystoreFile = File("${System.getProperty("user.dir")}/documents/keystore.jks")
    val debug = try {
        System.getenv("DEBUG") == "true"
    } catch (_: Exception) {
        false
    }
    val alias = try {
        System.getenv("KEY_ALIAS")
    } catch (_: Exception) {
        "worldcleanupday.at"
    }

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
            sslConnector(
                keyStore = KeyStore.getInstance(keystoreFile, getSystemCharArray("KEYSTORE_PASSWORD")),
                keyAlias = alias,
                keyStorePassword = { getSystemCharArray("KEYSTORE_PASSWORD") },
                privateKeyPassword = { getSystemCharArray("KEYSTORE_PASSWORD") }) {
                port = 8443
                keyStorePath = keystoreFile
            }
            module {
                body(debug)
            }
        }
    }

    embeddedServer(Netty, environment).start(wait = true)
}
