package ke.don.data.helper

import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.reflect.typeInfo
import ke.don.domain.model.ApiResponse
import ke.don.domain.model.NetworkError
import ke.don.domain.model.NetworkErrorCategory
import ke.don.domain.model.PatientResult
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.SocketTimeoutException
import kotlin.text.get

internal suspend inline fun <reified T> klient(
    crossinline call: suspend () -> HttpResponse,
): PatientResult<T, NetworkError> = try {
    val response = call()
    val statusCode = response.status.value

    if (statusCode in 200..299) {
        val body = response.body<ApiResponse<T>>()
        if (body.data != null) {
            PatientResult.Success(body.data)
        } else {
            PatientResult.Error(
                NetworkError(
                    category = NetworkErrorCategory.SERIALIZATION,
                    message = "Missing data field",
                    code = statusCode,
                )
            )
        }
    } else {
        val errorBody = response.bodyAsText()
        val json = try {
            Json.parseToJsonElement(errorBody).jsonObject
        } catch (_: Exception) {
            null
        }

        val message = when {
            json == null -> errorBody
            "errors" in json -> {
                val errors = json["errors"]!!.jsonObject
                buildString {
                    json["message"]?.jsonPrimitive?.contentOrNull?.let { appendLine(it) }
                    errors.forEach { (field, msgs) ->
                        val joined = msgs.jsonArray.joinToString("; ") { it.jsonPrimitive.content }
                        appendLine("$field: $joined")
                    }
                }.trim()
            }
            "message" in json -> json["message"]!!.jsonPrimitive.content
            else -> errorBody
        }

        PatientResult.Error(
            NetworkError(
                category = statusCode.toCategory(),
                message = "$statusCode: $message",
                code = statusCode,
            )
        )
    }
} catch (e: Exception) {
    PatientResult.Error(
        NetworkError(
            category = e.toCategory(),
            message = e.message,
            code = null,
        ),
    )
}



/**
 * Converts an HTTP status code to a [NetworkErrorCategory].
 *
 * This function maps common HTTP error codes to predefined categories,
 * simplifying error handling and categorization.
 *
 * @return The corresponding [NetworkErrorCategory] for the given HTTP status code.
 *         Returns [NetworkErrorCategory.UNKNOWN] if the status code doesn't match
 *         any predefined category.
 */
private fun Int.toCategory(): NetworkErrorCategory = when (this) {
    408 -> NetworkErrorCategory.REQUEST_TIMEOUT // Request Timeout
    401 -> NetworkErrorCategory.UNAUTHORIZED // Unauthorized
    409 -> NetworkErrorCategory.CONFLICT // Conflict
    429 -> NetworkErrorCategory.TOO_MANY_REQUESTS // Rate limiting
    413 -> NetworkErrorCategory.PAYLOAD_TOO_LARGE // Payload Too Large
    in 500..599 -> NetworkErrorCategory.SERVER_ERROR // Server-side errors
    else -> NetworkErrorCategory.UNKNOWN
}

/**
 * Converts an [Exception] to a [NetworkErrorCategory].
 *
 * This function helps categorize network-related exceptions into meaningful error types
 * for better error handling and user feedback.
 *
 * @return The corresponding [NetworkErrorCategory] for the exception.
 */
private fun Exception.toCategory(): NetworkErrorCategory = when (this) {
    is ConnectTimeoutException,
    is SocketTimeoutException,
        -> NetworkErrorCategory.REQUEST_TIMEOUT
    is IOException -> NetworkErrorCategory.NO_INTERNET
    is SerializationException -> NetworkErrorCategory.SERIALIZATION
    else -> NetworkErrorCategory.UNKNOWN
}