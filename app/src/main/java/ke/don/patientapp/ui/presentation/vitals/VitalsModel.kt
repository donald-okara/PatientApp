package ke.don.patientapp.ui.presentation.vitals

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
import kotlin.math.round

class VitalsModel(
    private val api: PatientApi,
    private val db: LocalDatabase
): ScreenModel {
    private val _uiState = MutableStateFlow(VitalsState())
    val uiState = _uiState.asStateFlow()

    lateinit var visitDates: List<String>

    fun handleIntent(intent: VitalsIntent){
        when(intent){
            is VitalsIntent.UpdatePatientId -> updatePatientId(intent.patientId)
            is VitalsIntent.Submit -> submit(intent.navigateToGeneral, intent.navigateToOverweight)
            is VitalsIntent.UpdateHeight -> updateHeight(intent.height)
            is VitalsIntent.UpdateVisitDate -> _uiState.update {
                it.copy(vitals = it.vitals.copy(visitDate = intent.date))
            }
            is VitalsIntent.UpdateWeight -> updateWeight(intent.weight)
        }
    }

    fun updatePatientId(patientId: String){
        _uiState.update {
            it.copy(
                vitals = it.vitals.copy(
                    patientId = patientId,

                )
            )
        }
        visitDates = db.getVitalsByPatient(patientId).map { it.visitDate }
    }

    fun submit(navigateToGeneral: (String) -> Unit, navigateToOverweight: (String) -> Unit){
        screenModelScope.launch {
            if (validateFields()){
                _uiState.update { it.copy(isLoading = true) }

                api.registerVitals(vitals = uiState.value.vitals).onSuccess { result ->
                    _uiState.update { it.copy(isLoading = false, isError = false) }
                    db.registerVitals(uiState.value.vitals)
                    if (uiState.value.vitals.bmi.toInt() < 25){
                        navigateToGeneral(uiState.value.vitals.patientId)
                    } else {
                        navigateToOverweight(uiState.value.vitals.patientId)
                    }
                }.onError { error ->
                    _uiState.update {
                        it.copy(isLoading = false, isError = true, errorMessage = error.message)
                    }
                }
            }
        }
    }

    fun updateHeight(height: Int) {
        val heightInMeters = height / 100.0
        _uiState.update {
            val weight = it.vitals.weight.toDoubleOrNull() ?: 0.0
            val bmiValue = if (heightInMeters > 0) weight / (heightInMeters * heightInMeters) else 0.0
            val bmi = round(bmiValue).toInt() // round to nearest integer

            it.copy(
                vitals = it.vitals.copy(
                    height = height.toString(),
                    bmi = bmi.toString()
                )
            )
        }
    }

    fun updateWeight(weight: Int) {
        _uiState.update {
            val height = it.vitals.height.toDoubleOrNull() ?: 0.0
            val heightInMeters = height / 100.0
            val bmiValue = if (heightInMeters > 0) weight / (heightInMeters * heightInMeters) else 0.0
            val bmi = round(bmiValue).toInt()

            it.copy(
                vitals = it.vitals.copy(
                    weight = weight.toString(),
                    bmi = bmi.toString()
                )
            )
        }
    }

    fun validateFields(): Boolean {
        val state = uiState.value
        _uiState.update {
            it.copy(
                visitDateError = if (state.vitals.visitDate.isBlank()) "Visit date is required" else if (visitDates.contains(state.vitals.visitDate))"You already have an assessment on this day" else null,
                heightError = if (state.vitals.height.toInt() <= 1 ) "height has to be more than 1" else null,
                weightError = if (state.vitals.weight.toInt() <= 1 ) "weight has to be more than 1" else null,
            )
        }

        // After updating errors, check if any of them are not null
        return uiState.value.let {
            it.visitDateError == null && it.heightError == null && it.weightError == null
        }
    }


}