package ke.don.patientapp.ui.presentation.auth

data class SignInState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,


    val passwordError: String? = null,
    val emailError: String? = null,
)