package ke.don.domain.model.tables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Vitals(
    @SerialName("visit_date") val visitDate: String = "",
    @SerialName("height") val height: String = 0.toString(),
    @SerialName("weight") val weight: String = 0.toString(),
    @SerialName("bmi") val bmi: String = "",
    @SerialName("patient_id") val patientId: String = ""
)