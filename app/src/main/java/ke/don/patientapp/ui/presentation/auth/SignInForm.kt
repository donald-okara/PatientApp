package ke.don.patientapp.ui.presentation.auth

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ke.don.patientapp.ui.presentation.components.ButtonToken
import ke.don.patientapp.ui.presentation.components.OutlinedInputField
import ke.don.patientapp.ui.presentation.components.PasswordInputField
import ke.don.patientapp.ui.presentation.patients.PatientList
import org.koin.compose.getKoin

class SignInForm: Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
        override fun Content() {

            val navigator = LocalNavigator.currentOrThrow
            val koin = getKoin()
            val screenModel = rememberScreenModel {
                koin.get<SignInModel>()
            }

            val state by screenModel.uiState.collectAsState()
            val onEvent = screenModel::handleIntent

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(text = "Sign up") },
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
                    SignInFormContent(
                        state = state,
                        onEvent = onEvent,
                        navigateToSignUp = {
                            navigator.push(SignUpForm())
                        },
                        onNavigateToPatients = {
                            navigator.replaceAll(PatientList())
                        }
                    )
                }

            }
        }
}

@Composable
fun SignInFormContent(
    modifier: Modifier = Modifier,
    state: SignInState,
    onEvent: (SignInIntent) -> Unit,
    onNavigateToPatients: () -> Unit,
    navigateToSignUp: () -> Unit
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        OutlinedInputField(
            value = state.email,
            onValueChange = { onEvent(SignInIntent.UpdateEmail(it)) },
            label = "Email",
            placeholder = "Enter your email",
            error = state.emailError != null,
            errorMessage = state.emailError,
            singleLine = true,
        )

        PasswordInputField(
            value = state.password,
            onValueChange = { onEvent(SignInIntent.UpdatePassword(it)) },
            label = "Password",
            placeholder = "Enter your password",
            error = state.passwordError != null,
            errorMessage = state.passwordError,
            singleLine = true
        )

        TextButton(
            onClick = navigateToSignUp,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Sign Up")
        }
        ButtonToken(
            text = "Sign In",
            onClick = {onEvent(SignInIntent.SignIn(onNavigateToPatients))},
            enabled = !state.isLoading,
            loading = state.isLoading,
            errorMessage = state.errorMessage
        )
    }
}