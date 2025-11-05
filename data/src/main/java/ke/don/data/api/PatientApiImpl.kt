package ke.don.data.api

import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ke.don.data.api.KtorClientProvider.client
import ke.don.data.helper.klient
import ke.don.data.local.datastore.authorisationDataStore
import ke.don.domain.model.NetworkError
import ke.don.domain.model.PatientResult
import ke.don.domain.model.SignInRequest
import ke.don.domain.model.SignUpRequest
import ke.don.domain.model.tables.Authorisation
import ke.don.domain.model.tables.Patient
import ke.don.domain.model.tables.Visit
import ke.don.domain.model.tables.Vitals
import ke.don.domain.repo.PatientApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PatientApiImpl(
    val context: Context,
): PatientApi {
    private val scope = CoroutineScope(Dispatchers.IO)
    lateinit var token: String

    init {
        scope.launch {
            token = context.authorisationDataStore.data.first().accessToken
        }
    }
    override suspend fun signUp(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
    ): PatientResult<Unit, NetworkError> = klient {
        client.post(Endpoint.Auth.SignUp.url){
            contentType(ContentType.Application.Json)
            setBody(
                SignUpRequest(
                    email = email,
                    firstname = firstName,
                    lastname = lastName,
                    password = password
                )
            )
        }
    }

    override suspend fun signIn(
        email: String,
        password: String,
    ): PatientResult<Authorisation, NetworkError> = klient {
        client.post(Endpoint.Auth.SignIn.url){
            contentType(ContentType.Application.Json)
            setBody(
                SignInRequest(
                    email = email,
                    password = password
                )
            )
        }
    }

    override suspend fun registerPatient(patient: Patient): PatientResult<Unit, NetworkError> = klient{
        client.post(Endpoint.Patient.Register.url){
            header("Authorization", "Bearer $token")
            // Or using the ktor helper
            // bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(patient)
        }
    }

    override suspend fun registerVitals(vitals: Vitals): PatientResult<Unit, NetworkError> = klient {
        client.post(Endpoint.Vitals.Add.url){
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(vitals)
        }
    }

    override suspend fun registerVisit(visit: Visit): PatientResult<Unit, NetworkError> = klient {
        client.post(Endpoint.Visit.Add.url){
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(visit)
        }
    }

    override suspend fun getPatients(): PatientResult<List<Patient>, NetworkError> = klient {
        client.post(Endpoint.Patient.List.url){
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }
    }
}