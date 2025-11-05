package ke.don.patientapp.ui.presentation.vitals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ke.don.patientapp.ui.presentation.components.ButtonToken
import ke.don.patientapp.ui.presentation.components.DatePickerTextField
import ke.don.patientapp.ui.presentation.components.OutlinedInputField
import ke.don.patientapp.ui.presentation.visit.VisitForm
import org.koin.compose.getKoin

class VitalsForm(private val patientId: String): Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val koin = getKoin()
        val screenModel = rememberScreenModel {
            koin.get<VitalsModel>()
        }

        val state by screenModel.uiState.collectAsState()
        val onEvent = screenModel::handleIntent

        LaunchedEffect(screenModel) {
            onEvent(VitalsIntent.UpdatePatientId(patientId))
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { navigator.pop() }
                        ){
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier
                            )
                        }
                    },
                    title = { Text(text = "Vitals form") },
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
                VitalsFormContent(
                    state = state,
                    onEvent = onEvent,
                    navigateToGeneral = {
                        navigator.push(VisitForm(it, false))
                    },
                    navigateToOverweight = {
                        navigator.push(VisitForm(it, true))
                    }
                )
            }

        }
    }
}

@Composable
fun VitalsFormContent(
    modifier: Modifier = Modifier,
    state: VitalsState,
    onEvent: (VitalsIntent) -> Unit,
    navigateToGeneral: (String) -> Unit,
    navigateToOverweight: (String) -> Unit
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        OutlinedInputField(
            value = state.vitals.patientId,
            onValueChange = { onEvent(VitalsIntent.UpdatePatientId(it)) },
            label = "Patient ID",
            placeholder = "Patient ID",
            singleLine = true
        )

        DatePickerTextField(
            value = state.vitals.visitDate,
            onValueChange = { onEvent(VitalsIntent.UpdateVisitDate(it)) },
            label = "Visit date",
            error = state.visitDateError != null,
            errorMessage = state.visitDateError,
        )

        OutlinedInputField(
            value = state.vitals.height,
            onValueChange = {
                val height = it.toIntOrNull()
                if (height != null) onEvent(VitalsIntent.UpdateHeight(height))
            },
            label = "Height",
            placeholder = "Height in cm",
            error = state.heightError != null,
            errorMessage = state.heightError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        OutlinedInputField(
            value = state.vitals.weight,
            onValueChange = {
                val weight = it.toIntOrNull()
                if (weight != null) onEvent(VitalsIntent.UpdateWeight(weight))
            },
            label = "Weight",
            placeholder = "Enter weight in kgs",
            error = state.weightError != null,
            errorMessage = state.weightError,
            singleLine = true
        )

        Text(
            text = "BMI: ${state.vitals.bmi}",
            modifier = Modifier.padding(8.dp)
        )

        ButtonToken(
            text = "Register",
            onClick = {
                onEvent(
                    VitalsIntent.Submit(
                        navigateToGeneral = navigateToGeneral,
                        navigateToOverweight = navigateToOverweight
                    )
                )
            },
            enabled = !state.isLoading,
            loading = state.isLoading
        )
    }
}