package ke.don.data.di

import ke.don.data.api.PatientApiImpl
import ke.don.data.local.database.DatabaseFactory
import ke.don.data.local.database.LocalDatabase
import ke.don.data.local.datastore.AuthorisationStore
import ke.don.domain.repo.PatientApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val datasourceModule = module{
    singleOf(::LocalDatabase)
    singleOf(::DatabaseFactory)
    singleOf(::AuthorisationStore)
    singleOf(::PatientApiImpl).bind<PatientApi>()
}