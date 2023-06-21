import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.AdminDao
import model.CleanupDayDao
import org.slf4j.LoggerFactory
import utils.*
import java.io.File
import java.security.KeyStore
import kotlin.random.Random
import kotlin.time.Duration.Companion.days

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
        "greenheroes.at"
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
                keyStore = KeyStore.getInstance(keystoreFile, System.getenv("KEYSTORE_PASSWORD").toCharArray()),
                keyAlias = alias,
                keyStorePassword = { System.getenv("KEYSTORE_PASSWORD").toCharArray() },
                privateKeyPassword = { System.getenv("KEYSTORE_PASSWORD").toCharArray() }) {
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

        logInfo("password is" + (AdminDao.getLatest()?.let { ": ${it.password}" } ?: " empty"))

        val password = AdminDao.getLatest()?.password ?: run {
            val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            val random = Random(System.currentTimeMillis())
            val newPass = (1..10).map {
                random.nextInt(0, charPool.size).let { charPool[it] }
            }.joinToString("")

            AdminDao.insert(newPass)
            logInfo("generated new password: $newPass")

            newPass
        }

        // TODO remove again -> just here for debugging
        CleanupDayDao.getNext() ?: CleanupDayDao.insert(Clock.System.now().plus(1.days).toLocalDateTime(TimeZone.currentSystemDefault()))

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

        installRouting()
    }
}