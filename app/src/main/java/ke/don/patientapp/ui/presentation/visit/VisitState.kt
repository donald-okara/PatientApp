package ke.don.patientapp.ui.presentation.visit

import ke.don.domain.model.tables.Visit

data class VisitState(
    val visit: Visit = Visit(),

    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,

    val visitDateError: String? = null,
    val healthError: String? = null,
    val dietError: String? = null,
    val drugsError: String? = null,
    val commentsError: String? = null
)