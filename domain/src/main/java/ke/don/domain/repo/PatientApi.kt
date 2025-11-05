package ke.don.domain.repo

import ke.don.domain.model.NetworkError
import ke.don.domain.model.PatientResult
import ke.don.domain.model.tables.Authorisation
import ke.don.domain.model.tables.Patient
import ke.don.domain.model.tables.Visit
import ke.don.domain.model.tables.Vitals

interface PatientApi {
    suspend fun signUp(email: String, firstName: String, lastName: String, password: String): PatientResult<Unit, NetworkError>
    suspend fun signIn(email: String, password: String): PatientResult<Authorisation, NetworkError>

    suspend fun registerPatient(patient: Patient) : PatientResult<Unit, NetworkError>
    suspend fun registerVitals(vitals: Vitals): PatientResult<Unit, NetworkError>
    suspend fun registerVisit(visit: Visit): PatientResult<Unit, NetworkError>
    suspend fun getPatients(): PatientResult<List<Patient>, NetworkError>

}