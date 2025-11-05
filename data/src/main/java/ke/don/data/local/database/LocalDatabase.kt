package ke.don.data.local.database

import ke.don.data.PatientDatabase
import ke.don.data.Patient_enity
import ke.don.data.Visit_entity
import ke.don.data.Vital_entity
import ke.don.domain.model.tables.Patient
import ke.don.domain.model.tables.Visit
import ke.don.domain.model.tables.Vitals

class LocalDatabase(
    databaseFactory: DatabaseFactory,
) {
    private val database = PatientDatabase(databaseFactory.createDriver())
    val patientQueries = database.patient_entityQueries
    val visitQueries = database.visit_entityQueries
    val vitalsQueries = database.vital_entityQueries

    fun registerPatient(patient: Patient){
        patientQueries.addOrUpdatePatient(
            firstName = patient.firstName,
            secondName = patient.lastName,
            uniqueId = patient.unique,
            dob = patient.dob,
            gender = patient.gender,
            regDate = patient.regDate
        )
    }

    fun registerVitals(vitals: Vitals){
        vitalsQueries.registerVital(
            visitDate = vitals.visitDate,
            height = vitals.height,
            weight = vitals.weight,
            bmi = vitals.bmi,
            patientId = vitals.patientId
        )
    }

    fun registerVisit(visit: Visit){
        visitQueries.registerVisit(
            generalHealth = visit.generalHealth,
            onDiet = visit.onDiet,
            onDrugs = visit.onDrugs,
            comments = visit.comments,
            visitDate = visit.visitDate,
            vitalId = visit.vitalId,
            patientId = visit.patientId
        )
    }

    fun getVitalsByPatient(patientId: String): List<Vitals>{
        return vitalsQueries.getVitalByPatient(patientId).executeAsList().map { it.toDomain() }
    }

    fun getVisitsByVital(patientId: String): List<Visit>{
        return visitQueries.getVisitByVital(patientId).executeAsList().map { it.toDomain() }
    }

    fun getPatients(): List<Patient>{
        return patientQueries.getAllPatients().executeAsList().map { it.toDomain() }
    }
}


fun Patient_enity.toDomain(): Patient{
    return Patient(
        firstName = first_name,
        lastName = second_name,
        unique = unique_id,
        dob = dob,
        gender = gender,
        regDate = reg_date
    )
}

fun Vital_entity.toDomain() : Vitals {
    return Vitals(
        visitDate = visit_date,
        height = height,
        weight = weight,
        bmi = bmi,
        patientId = patient_id
    )
}

fun Visit_entity.toDomain() : Visit {
    return Visit(
        generalHealth = general_health,
        onDiet = on_diet,
        onDrugs = on_drugs,
        comments = comments,
        visitDate = visit_date,
        vitalId = vital_id,
        patientId = patient_id
    )
}