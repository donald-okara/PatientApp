package ke.don.patientapp.ui.presentation.patients

import ke.don.domain.model.tables.Patient

data class PatientListState (
    val patients: List<Patient> = emptyList(),
)

