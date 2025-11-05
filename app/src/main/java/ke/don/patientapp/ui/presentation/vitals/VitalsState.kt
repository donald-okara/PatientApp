package ke.don.patientapp.ui.presentation.vitals

import ke.don.domain.model.tables.Vitals

data class VitalsState(
    val vitals: Vitals = Vitals(),

    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String?= null,

    val visitDateError: String? = null,
    val heightError: String? = null,
    val weightError: String? = null,
)