package ke.don.domain.model.tables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Visit(
    @SerialName("general_health") val generalHealth: String = "",
    @SerialName("on_diet") val onDiet: String = "",
    @SerialName("on_drugs") val onDrugs: String = "",
    @SerialName("comments") val comments: String = "",
    @SerialName("visit_date") val visitDate: String = "",
    @SerialName("patient_id") val patientId: String = "",
    @SerialName("vital_id") val vitalId: String = ""
)