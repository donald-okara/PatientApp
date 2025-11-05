package ke.don.data.helper

import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.reflect.typeInfo
import ke.don.domain.model.NetworkError
import ke.don.domain.model.NetworkErrorCategory
import ke.don.domain.model.PatientResult
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
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
        // Pass typeInfo to help Ktor decode lists properly
        PatientResult.Success(response.body(typeInfo<T>()) as T)
    } else {
        val errorBody = response.bodyAsText()
        val errorMessage = try {
            val json = Json.parseToJsonElement(errorBody).jsonObject
            json["message"]?.jsonPrimitive?.content ?: response.status.description
        } catch (e: Exception) {
            response.status.description
        }
        PatientResult.Error(
            NetworkError(
                category = statusCode.toCategory(),
                message = errorMessage,
                code = statusCode,
            ),
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