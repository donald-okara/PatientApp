package ke.don.patientapp.ui.presentation.visit

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
import ke.don.domain.model.tables.Visit
import ke.don.patientapp.ui.presentation.components.ButtonToken
import ke.don.patientapp.ui.presentation.components.DatePickerTextField
import ke.don.patientapp.ui.presentation.components.DropdownTextField
import ke.don.patientapp.ui.presentation.components.OutlinedInputField
import ke.don.patientapp.ui.presentation.vitals.VitalsIntent
import ke.don.patientapp.ui.presentation.vitals.VitalsModel
import ke.don.patientapp.ui.presentation.vitals.VitalsState
import org.koin.compose.getKoin

class VisitForm(private val patientId: String, private val isOverweight: Boolean): Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val koin = getKoin()
        val screenModel = rememberScreenModel {
            koin.get<VisitModel>()
        }

        val state by screenModel.uiState.collectAsState()
        val onEvent = screenModel::handleIntent


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
                    title = { Text(text = if(isOverweight) "Overweight form" else "General form") },
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
                VisitFormContent(
                    state = state,
                    onEvent = onEvent,
                    navigateToPatientList = {
                        navigator.pop()
                    },
                    isOverweight = isOverweight
                )
            }

        }
    }
}

@Composable
fun VisitFormContent(
    modifier: Modifier = Modifier,
    state: VisitState,
    onEvent: (VisitIntent) -> Unit,
    navigateToPatientList: () -> Unit,
    isOverweight: Boolean
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        DatePickerTextField(
            value = state.visit.visitDate,
            onValueChange = { onEvent(VisitIntent.UpdateVisitDate(it)) },
            label = "Visit date",
            error = state.visitDateError != null,
            errorMessage = state.visitDateError,
        )

        DropdownTextField(
            label = "General health",
            options = listOf("Good", "Poor"),
            value = state.visit.generalHealth,
            onValueChange = { onEvent(VisitIntent.Health(it))},
            error = state.healthError != null,
            errorMessage = state.healthError
        )


        if (isOverweight){
            Text(
                text = "Have you been on a diet?",
                modifier = Modifier.padding(8.dp)
            )
            DropdownTextField(
                label = "Answer",
                options = listOf("Yes", "No"),
                value = state.visit.onDiet,
                onValueChange = { onEvent(VisitIntent.Diet(it)) },
                error = state.dietError != null,
                errorMessage = state.dietError
            )
        } else {
            Text(
                text = "Are you currently taking any drugs?",
                modifier = Modifier.padding(8.dp)
            )
            DropdownTextField(
                label = "Answer",
                options = listOf("Yes","No"),
                value = state.visit.onDrugs,
                onValueChange = { onEvent(VisitIntent.Drugs(it)) },
                error = state.drugsError != null,
                errorMessage = state.drugsError
            )
        }

        OutlinedInputField(
            value = state.visit.comments,
            onValueChange = { onEvent(VisitIntent.Comments(it)) },
            label = "Comments",
            error = state.commentsError != null,
            errorMessage = state.commentsError,
            singleLine = true
        )

        ButtonToken(
            text = "Register",
            onClick = {
                onEvent(
                    VisitIntent.Submit(
                        navigateToPatientList
                    )
                )
            },
            enabled = !state.isLoading,
            loading = state.isLoading
        )
    }
}