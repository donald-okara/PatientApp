package ke.don.patientapp.ui.presentation.auth

data class SignInState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,

    val passwordError: String? = null,
    val emailError: String? = null,
)