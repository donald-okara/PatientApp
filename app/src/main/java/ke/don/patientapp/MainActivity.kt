package ke.don.patientapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import ke.don.data.local.datastore.AuthorisationStore
import ke.don.data.local.datastore.authorisationDataStore
import ke.don.patientapp.ui.presentation.auth.SignInForm
import ke.don.patientapp.ui.presentation.patients.PatientList
import ke.don.patientapp.ui.theme.PatientAppTheme
import kotlinx.coroutines.flow.first
import org.koin.compose.getKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PatientAppTheme {
                val koin = getKoin()
                val authStore = koin.get<AuthorisationStore>()

                var isSignedIn by remember { mutableStateOf<Boolean?>(null) }

                LaunchedEffect(Unit) {
                    val authorisation = authStore.current()
                    Log.d("MainActivity", "onCreate: $authorisation")
                    isSignedIn = authorisation.accessToken.isNotBlank()
                }

                when (isSignedIn) {
                    null -> {}
                    true -> Navigator(PatientList())
                    false -> Navigator(SignInForm())
                }
            }
        }
    }
}
