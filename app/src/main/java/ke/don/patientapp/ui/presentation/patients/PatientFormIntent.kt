package ke.don.patientapp.ui.presentation.patients

import ke.don.domain.model.tables.Patient

sealed interface PatientFormIntent {
    class UpdateFirstName(val firstName: String) : PatientFormIntent
    class UpdateLastName(val lastName: String) : PatientFormIntent
    class UpdateUnique(val unique: String): PatientFormIntent
    class UpdateDOB(val dob: String): PatientFormIntent
    class UpdateGender(val gender: String): PatientFormIntent
    class UpdateRegDate(val regDate: String): PatientFormIntent
    class Submit(val navigateToVitals: (String) -> Unit): PatientFormIntent
}