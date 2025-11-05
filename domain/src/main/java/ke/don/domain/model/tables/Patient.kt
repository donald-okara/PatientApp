package ke.don.domain.model.tables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Patient(
    @SerialName("firstname") val firstName: String = "",
    @SerialName("lastname") val lastName: String = "",
    @SerialName("unique") val unique: String = "",
    @SerialName("dob") val dob: String = "",
    @SerialName("gender") val gender: String = "", //Could be enum, docs not clear
    @SerialName("reg_date") val regDate: String = ""
)