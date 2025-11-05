package ke.don.patientapp.ui.presentation.patients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ke.don.patientapp.ui.presentation.auth.SignUpIntent
import ke.don.patientapp.ui.presentation.auth.SignUpState
import ke.don.patientapp.ui.presentation.components.ButtonToken
import ke.don.patientapp.ui.presentation.components.DatePickerTextField
import ke.don.patientapp.ui.presentation.components.DropdownTextField
import ke.don.patientapp.ui.presentation.components.OutlinedInputField
import ke.don.patientapp.ui.presentation.components.PasswordInputField
import ke.don.patientapp.ui.presentation.vitals.VitalsForm
import org.koin.compose.getKoin

class PatientForm: Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val koin = getKoin()
        val screenModel = rememberScreenModel {
            koin.get<PatientFormModel>()
        }

        val state by screenModel.uiState.collectAsState()
        val onEvent = screenModel::handleIntent


        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Patient form") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                PatientFormContent(
                    state = state,
                    onEvent = onEvent,
                    navigateToVitals = {
                        navigator.push(VitalsForm(it))
                    }
                )
            }

        }
    }
}

@Composable
fun PatientFormContent(
    modifier: Modifier = Modifier,
    state: PatientFormState,
    onEvent: (PatientFormIntent) -> Unit,
    navigateToVitals: (String) -> Unit
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        OutlinedInputField(
            value = state.patient.unique,
            onValueChange = { onEvent(PatientFormIntent.UpdateUnique(it)) },
            label = "Patient number",
            placeholder = "Enter your patient number",
            error = state.uniqueError != null,
            errorMessage = state.uniqueError,
            singleLine = true,
        )

        DatePickerTextField(
            value = state.patient.regDate,
            onValueChange = { onEvent(PatientFormIntent.UpdateRegDate(it)) },
            label = "Registration date",
            error = state.regDateError != null,
            errorMessage = state.regDateError,
        )

        OutlinedInputField(
            value = state.patient.firstName,
            onValueChange = { onEvent(PatientFormIntent.UpdateFirstName(it)) },
            label = "First name",
            placeholder = "Enter first name",
            error = state.firstNameError != null,
            errorMessage = state.firstNameError,
            singleLine = true
        )

        OutlinedInputField(
            value = state.patient.lastName,
            onValueChange = { onEvent(PatientFormIntent.UpdateLastName(it)) },
            label = "Last name",
            placeholder = "Enter last name",
            error = state.lastNameError != null,
            errorMessage = state.lastNameError,
            singleLine = true
        )

        DatePickerTextField(
            value = state.patient.dob,
            onValueChange = { onEvent(PatientFormIntent.UpdateDOB(it)) },
            label = "Date of birth",
            error = state.dobError != null,
            errorMessage = state.dobError,
        )

        DropdownTextField(
            label = "Gender",
            options = listOf("Male", "Female", "Other"),
            value = state.patient.gender,
            onValueChange = { onEvent(PatientFormIntent.UpdateGender(it)) },
            error = state.genderError != null,
            errorMessage = state.genderError
        )

        ButtonToken(
            text = "Register",
            onClick = {onEvent(PatientFormIntent.Submit(navigateToVitals))},
            enabled = !state.isLoading,
            loading = state.isLoading
        )
    }
}
