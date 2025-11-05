package ke.don.patientapp.ui.presentation.vitals

sealed interface VitalsIntent {
    class UpdatePatientId(val patientId: String): VitalsIntent
    class Submit(val navigateToGeneral: (String) -> Unit, val navigateToOverweight: (String) -> Unit): VitalsIntent
    class UpdateVisitDate(val date: String): VitalsIntent
    class UpdateHeight(val height: Int): VitalsIntent
    class UpdateWeight(val weight: Int): VitalsIntent
}