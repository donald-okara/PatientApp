package ke.don.patientapp.ui.presentation.patients

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import ke.don.patientapp.ui.presentation.auth.SignInFormContent
import ke.don.patientapp.ui.presentation.auth.SignInIntent
import ke.don.patientapp.ui.presentation.auth.SignInModel
import ke.don.patientapp.ui.presentation.auth.SignInState
import ke.don.patientapp.ui.presentation.components.ButtonToken
import ke.don.patientapp.ui.presentation.components.OutlinedInputField
import ke.don.patientapp.ui.presentation.components.PasswordInputField
import ke.don.patientapp.ui.presentation.components.PatientCard
import ke.don.patientapp.ui.presentation.vitals.VitalsForm
import org.koin.compose.getKoin

class PatientList: Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val koin = getKoin()
        val screenModel = rememberScreenModel {
            koin.get<PatientListModel>()
        }

        val state by screenModel.uiState.collectAsState()
        val onEvent = screenModel::handleIntent

        LaunchedEffect(screenModel) {
            onEvent(PatientListIntent.FetchPatients)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Patients") },
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
                PatientListContent(
                    state = state,
                    navigateToVitals = {
                        navigator.push(VitalsForm(it))
                    },
                    navigateToPatientForm = {
                        navigator.push(PatientForm())
                    },
                )
            }

        }
    }
}

@Composable
fun PatientListContent(
    modifier: Modifier = Modifier,
    state: PatientListState,
    navigateToVitals: (String) -> Unit,
    navigateToPatientForm: () -> Unit
){
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {

        item {
            AnimatedVisibility(
                visible = state.patients.isEmpty()
            ){
                ButtonToken(
                    text = "Add Patient",
                    onClick = navigateToPatientForm,
                )
            }
        }

        items(state.patients){
            PatientCard(
                patient = it,
                onClick = { navigateToVitals(it.unique) }
            )
        }
    }
}
