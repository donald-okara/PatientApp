package ke.don.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val message: String? = null,
    val success: Boolean? = null,
    val code: Int? = null,
    val data: T
)

