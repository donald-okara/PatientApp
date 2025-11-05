package ke.don.patientapp.ui.presentation.patients

import android.os.Message
import ke.don.domain.model.tables.Patient

data class PatientFormState(
    val patient: Patient = Patient(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,

    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val dobError: String? = null,
    val genderError: String? = null,
    val uniqueError: String? = null,
    val regDateError: String? = null
)