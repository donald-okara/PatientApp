package ke.don.patientapp.ui.presentation.auth

sealed interface SignInIntent {
    class UpdateEmail(val email: String) : SignInIntent
    class UpdatePassword(val password: String) : SignInIntent
    class SignIn(val navigateToPatients: () -> Unit) : SignInIntent
}