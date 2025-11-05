package ke.don.patientapp.ui.presentation.patients

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ke.don.data.local.database.LocalDatabase
import ke.don.domain.model.onError
import ke.don.domain.model.onSuccess
import ke.don.domain.repo.PatientApi
import ke.don.patientapp.ui.presentation.auth.SignUpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PatientFormModel(
    private val api: PatientApi,
    private val db: LocalDatabase
): ScreenModel {
    private val _uiState = MutableStateFlow(PatientFormState())
    val uiState = _uiState.asStateFlow()

    val patientsList = db.getPatients().map { it.unique }
    fun handleIntent(intent: PatientFormIntent){
        when(intent){
            is PatientFormIntent.Submit -> {
                submitPatient(intent.navigateToVitals)
            }
            is PatientFormIntent.UpdateDOB -> {
                _uiState.update { it.copy(patient = it.patient.copy(dob = intent.dob)) }
            }
            is PatientFormIntent.UpdateFirstName -> {
                _uiState.update { it.copy(patient = it.patient.copy(firstName = intent.firstName)) }
            }
            is PatientFormIntent.UpdateGender -> {
                _uiState.update { it.copy(patient = it.patient.copy(gender = intent.gender)) }
            }
            is PatientFormIntent.UpdateLastName -> {
                _uiState.update { it.copy(patient = it.patient.copy(lastName = intent.lastName)) }
            }
            is PatientFormIntent.UpdateRegDate -> {
                _uiState.update { it.copy(patient = it.patient.copy(regDate = intent.regDate)) }
            }
            is PatientFormIntent.UpdateUnique -> {
                _uiState.update { it.copy(patient = it.patient.copy(unique = intent.unique)) }
            }
        }
    }

    private fun submitPatient(navigateToVitals: (String) -> Unit) {
        screenModelScope.launch {
            if (validateFields()) {
                _uiState.update { it.copy(isLoading = true) }

                api.registerPatient(patient = uiState.value.patient)
                    .onSuccess { result ->
                        _uiState.update { it.copy(isLoading = false, isError = false) }
                        db.registerPatient(uiState.value.patient)
                        navigateToVitals(uiState.value.patient.unique)
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
                firstNameError = if (state.patient.firstName.isBlank()) "First name is required" else null,
                lastNameError = if (state.patient.lastName.isBlank()) "Last name is required" else null,
                dobError = if (state.patient.dob.isBlank()) "Date of birth is required" else null,
                genderError = if (state.patient.gender.isBlank()) "Gender is required" else null,
                uniqueError = if (state.patient.unique.isBlank()) "Unique identifier is required" else if (patientsList.contains(state.patient.unique)) "Patient is already registered" else null,
                regDateError = if (state.patient.regDate.isBlank()) "Registration date is required" else null
            )
        }

        // After updating errors, check if any of them are not null
        return uiState.value.let {
            it.firstNameError == null && it.lastNameError == null && it.dobError == null && it.genderError == null && it.uniqueError == null && it.regDateError == null
        }
    }
}

