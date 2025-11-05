package ke.don.patientapp.ui.presentation.visit

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ke.don.data.local.database.LocalDatabase
import ke.don.domain.model.onError
import ke.don.domain.model.onSuccess
import ke.don.domain.repo.PatientApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VisitModel(
    val db: LocalDatabase,
    val api: PatientApi
): ScreenModel {
    private val _uiState = MutableStateFlow(VisitState())
    val uiState = _uiState.asStateFlow()

    lateinit var vitalDates: List<String>

    fun handleIntent(intent: VisitIntent){
        when(intent){
            is VisitIntent.Comments -> _uiState.update {
                it.copy(visit = it.visit.copy(comments = intent.text))
            }
            is VisitIntent.Diet -> _uiState.update {
                it.copy(visit = it.visit.copy(onDiet = intent.text))
            }
            is VisitIntent.Drugs -> _uiState.update {
                it.copy(visit = it.visit.copy(onDrugs = intent.text))
            }
            is VisitIntent.Health -> _uiState.update {
                it.copy(visit = it.visit.copy(generalHealth = intent.text))
            }
            is VisitIntent.UpdateId -> updateId(intent.patientId, intent.vitalId, intent.isOverWeight)
            is VisitIntent.UpdateVisitDate -> _uiState.update {
                it.copy(visit = it.visit.copy(visitDate = intent.date))
            }
            is VisitIntent.Submit -> submit(intent.navigateToPatientList)
        }

    }

    fun submit(navigateToPatientList: () -> Unit){
        screenModelScope.launch {
            if (validateFields()) {
                _uiState.update { it.copy(isLoading = true) }

                api.registerVisit(visit = uiState.value.visit).onSuccess { result ->
                    _uiState.update { it.copy(isLoading = false, isError = false) }
                    db.registerVisit(uiState.value.visit)
                    navigateToPatientList()
                }.onError { error ->
                    _uiState.update {
                        it.copy(isLoading = false, isError = true, errorMessage = error.message)
                    }
                }
            }
        }
    }

    fun updateId(
        patientId: String,
        vitalId: String,
        isOverWeight: Boolean
    ){
        _uiState.update {
            it.copy(isOverWeight = isOverWeight, visit = it.visit.copy(patientId = patientId, vitalId = vitalId))
        }
        screenModelScope.launch {
            vitalDates = db.getVitalsByPatient(patientId).map { it.visitDate }
        }
    }

    fun validateFields(): Boolean {
        val state = uiState.value

        _uiState.update {
            it.copy(
                visitDateError = if (state.visit.visitDate.isBlank()) "This field is required" else if (state.visit.visitDate !in vitalDates) "This you already have a visit today" else null,
                healthError = if (state.visit.generalHealth.toInt() > 10) "This field is required" else null,
                dietError = if (state.visit.onDiet.isEmpty() && state.isOverWeight) "This field is required" else null,
                drugsError = if (state.visit.onDrugs.isEmpty() && !state.isOverWeight) "This field is required" else null,
                commentsError = if (state.visit.comments.toInt() > 10) "This field is required" else null
            )
        }
        // After updating errors, check if any of them are not null
        return uiState.value.let {
            it.visitDateError == null && it.dietError == null && it.drugsError == null && it.commentsError == null && it.healthError == null
        }
    }
}