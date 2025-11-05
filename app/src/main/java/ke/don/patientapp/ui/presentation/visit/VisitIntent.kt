package ke.don.patientapp.ui.presentation.visit

sealed interface VisitIntent {
    class UpdateId(val patientId: String, val vitalId: String, val isOverWeight: Boolean): VisitIntent
    class UpdateVisitDate(val date: String): VisitIntent
    class Health(val text: String): VisitIntent
    class Diet(val text: String): VisitIntent
    class Drugs(val text: String): VisitIntent
    class Comments(val text: String): VisitIntent
    class Submit(val navigateToPatientList: () -> Unit): VisitIntent
}