package ke.don.patientapp.ui.presentation.patients

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

class PatientListModel(
    private val db: LocalDatabase,
    private val api: PatientApi
): ScreenModel {
    private val _uiState = MutableStateFlow(PatientListState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: PatientListIntent){
        when(intent){
            is PatientListIntent.FetchPatients -> fetchPatients()
        }
    }

    fun fetchPatients(){
        screenModelScope.launch {
            val patientList = db.getPatients()
            _uiState.update {
                it.copy(
                    patients = patientList
                )
            }
        }
    }
}