package ke.don.domain.model

data class NetworkError(
    val category: NetworkErrorCategory = NetworkErrorCategory.UNKNOWN,
    val message: String? = null,
    val code: Int? = null,
) : PatientError