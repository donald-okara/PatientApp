package ke.don.patientapp.ui.presentation.auth

import android.content.Context
import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ke.don.data.local.datastore.AuthorisationStore
import ke.don.data.local.datastore.setAuthorisation
import ke.don.domain.model.onError
import ke.don.domain.model.onSuccess
import ke.don.domain.repo.PatientApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInModel(
    private val authorisationStore: AuthorisationStore,
    private val api: PatientApi
): ScreenModel{
    private val _uiState = MutableStateFlow(SignInState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: SignInIntent){
        when(intent){
            is SignInIntent.UpdateEmail -> updateUsername(intent.email)
            is SignInIntent.UpdatePassword -> updatePassword(intent.password)
            is SignInIntent.SignIn -> signIn(intent.navigateToPatients)
        }
    }

    fun updateUsername(username: String){
        _uiState.update {
            it.copy(email = username)
        }
    }

    fun updatePassword(password: String){
        _uiState.update {
            it.copy(password = password)
        }
    }

    fun signIn(navigateToPatients: () -> Unit){
        screenModelScope.launch {
            if (validateFields()){
                _uiState.update {
                    it.copy(isLoading = true)
                }

                api.signIn(email = uiState.value.email, password = uiState.value.password)
                    .onSuccess { result ->
                        _uiState.update {
                            it.copy(isLoading = false, isError = false)
                        }
                        authorisationStore.save(result)
                        navigateToPatients()
                    }.onError { error ->
                        _uiState.update {
                            it.copy(isLoading = false, isError = true, errorMessage = error.message)
                        }
                    }
            }
        }

    }

    fun validateFields(): Boolean {
        val state = uiState.value
        _uiState.update {
            it.copy(
                passwordError = if (state.password.isBlank()) "Date of birth is required" else null,
                emailError = if (state.email.isBlank()) "Gender is required" else null,
            )
        }

        // After updating errors, check if any of them are not null
        return uiState.value.let {
            it.passwordError == null && it.emailError == null
        }
    }
}