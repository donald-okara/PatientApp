package ke.don.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val email: String,
    val firstname: String,
    val lastname: String,
    val password: String,
)

@Serializable
data class SignInRequest(
    val email: String,
    val password: String,
)
