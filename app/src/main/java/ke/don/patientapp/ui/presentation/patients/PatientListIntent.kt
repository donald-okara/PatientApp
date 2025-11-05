package ke.don.patientapp.ui.presentation.patients

sealed interface PatientListIntent {
    object FetchPatients: PatientListIntent
}