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
import org.koin.compose.getKoin

class SignUpForm: Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val koin = getKoin()
        val screenModel = rememberScreenModel {
            koin.get<SignUpModel>()
        }

        val state by screenModel.uiState.collectAsState()
        val onEvent = screenModel::handleIntent

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Sign In") },
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
                SignUpFormContent(
                    state = state,
                    onEvent = onEvent,
                    navigateToSignIn = {
                        navigator.push(SignInForm())
                    }
                )
            }

        }
    }
}

@Composable
fun SignUpFormContent(
    modifier: Modifier = Modifier,
    state: SignUpState,
    onEvent: (SignUpIntent) -> Unit,
    navigateToSignIn: () -> Unit
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
            onValueChange = { onEvent(SignUpIntent.UpdateEmail(it)) },
            label = "Email",
            placeholder = "Enter your email",
            error = state.emailError != null,
            errorMessage = state.emailError,
            singleLine = true,
        )
        OutlinedInputField(
            value = state.firstname,
            onValueChange = { onEvent(SignUpIntent.UpdateFirst(it)) },
            label = "First name",
            placeholder = "Enter your first name",
            error = state.firstNameError != null,
            errorMessage = state.firstNameError,
            singleLine = true,
        )
        OutlinedInputField(
            value = state.lastname,
            onValueChange = { onEvent(SignUpIntent.UpdateLast(it)) },
            label = "Second name",
            placeholder = "Enter your second name",
            error = state.lastNameError != null,
            errorMessage = state.lastNameError,
            singleLine = true,
        )

        PasswordInputField(
            value = state.password,
            onValueChange = { onEvent(SignUpIntent.UpdatePassword(it)) },
            label = "Password",
            placeholder = "Enter your password",
            error = state.passwordError != null,
            errorMessage = state.passwordError,
            singleLine = true
        )

        ButtonToken(
            text = "Sign Up",
            onClick = {onEvent(SignUpIntent.SignUp(navigateToSignIn))},
            enabled = !state.isLoading,
            loading = state.isLoading
        )
    }
}