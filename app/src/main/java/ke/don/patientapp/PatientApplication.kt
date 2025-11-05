package ke.don.patientapp

import android.app.Application
import ke.don.data.di.datasourceModule
import ke.don.patientapp.di.authModule
import ke.don.patientapp.di.patientModule
import ke.don.patientapp.di.visitModule
import ke.don.patientapp.di.vitalsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class PatientApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PatientApplication)
            modules(
                datasourceModule,
                authModule,
                patientModule,
                visitModule,
                vitalsModule
            )
        }
    }
}