package ke.don.patientapp.ui.presentation.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ke.don.domain.model.onSuccess
import ke.don.domain.repo.PatientApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpModel(
    private val api: PatientApi
): ScreenModel {
    private val _uiState = MutableStateFlow(SignUpState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: SignUpIntent){
        when(intent){
            is SignUpIntent.SignUp -> signUp(intent.navigateToSignIn)
            is SignUpIntent.UpdateEmail -> updateEmail(intent.email)
            is SignUpIntent.UpdateFirst -> updateFirst(intent.first)
            is SignUpIntent.UpdateLast -> updateLast(intent.last)
            is SignUpIntent.UpdatePassword -> updatePassword(intent.password)
        }
    }

    fun updateEmail(username: String){
        _uiState.update {
            it.copy(email = username)
        }
    }

    fun updatePassword(password: String){
        _uiState.update {
            it.copy(password = password)
        }
    }

    fun updateFirst(name: String){
        _uiState.update {
            it.copy(firstname = name)
        }
    }

    fun updateLast(name: String){
        _uiState.update {
            it.copy(lastname = name)
        }
    }

    fun signUp(navigateToSignIn: () -> Unit){
        screenModelScope.launch {
            if (validateFields()){
                _uiState.update {
                    it.copy(isLoading = true)
                }

                api.signUp(
                    email = uiState.value.email,
                    password = uiState.value.password,
                    firstName = uiState.value.firstname,
                    lastName = uiState.value.lastname
                ).onSuccess { result ->
                    _uiState.update {
                        it.copy(isLoading = false, isError = false)
                    }
                    navigateToSignIn()
                }
            }
        }

    }

    fun validateFields(): Boolean {
        val state = uiState.value
        _uiState.update {
            it.copy(
                firstNameError = if (state.firstname.isBlank()) "First name is required" else null,
                lastNameError = if (state.lastname.isBlank()) "Last name is required" else null,
                passwordError = if (state.password.isBlank()) "Password is required" else null,
                emailError = if (state.email.isBlank()) "email is required" else null,
            )
        }

        // After updating errors, check if any of them are not null
        return uiState.value.let {
            it.firstNameError == null && it.lastNameError == null && it.passwordError == null && it.emailError == null
        }
    }

}