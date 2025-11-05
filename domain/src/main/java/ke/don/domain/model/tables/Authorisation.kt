package ke.don.domain.model.tables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Authorisation(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("access_token") val accessToken: String = ""
)

