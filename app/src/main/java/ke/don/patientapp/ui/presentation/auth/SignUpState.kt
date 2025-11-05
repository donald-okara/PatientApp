package ke.don.patientapp.ui.presentation.auth

data class SignUpState (
    val email: String = "",
    val password: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,

    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val passwordError: String? = null,
    val emailError: String? = null,
)
