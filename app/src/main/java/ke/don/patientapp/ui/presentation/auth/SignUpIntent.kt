package ke.don.patientapp.ui.presentation.auth

sealed interface SignUpIntent {
    class UpdateEmail(val email: String) : SignUpIntent
    class UpdatePassword(val password: String) : SignUpIntent
    class UpdateFirst(val first: String) : SignUpIntent
    class UpdateLast(val last: String) : SignUpIntent
    class SignUp(val navigateToSignIn: () -> Unit) : SignUpIntent
}