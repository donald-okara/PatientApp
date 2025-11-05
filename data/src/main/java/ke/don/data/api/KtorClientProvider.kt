package ke.don.data.api

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.logging.Logger
import kotlinx.serialization.json.Json


internal object KtorClientProvider {
    /**
     * The [HttpClient] instance used for making network requests.
     * It is configured with:
     * - [HttpTimeout]: Sets timeouts for requests, connections, and sockets.
     * - [ContentNegotiation]: Configures JSON serialization/deserialization using Kotlinx Serialization.
     * - [Logging]: Enables logging of HTTP requests and responses.
     */
    // This will store the session provider, which we'll set manually
    val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000 // or higher
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }

        install(ContentNegotiation) {
            json(
                Json {
                    encodeDefaults = false
                    explicitNulls = false
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    coerceInputValues = true
                },
            )
        }


        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("KtorLogger", message)
                }
            }
            level = LogLevel.BODY
        }
    }
}