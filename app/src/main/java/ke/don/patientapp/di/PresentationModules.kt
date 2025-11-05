package ke.don.patientapp.di

import ke.don.data.di.datasourceModule
import ke.don.patientapp.ui.presentation.auth.SignInModel
import ke.don.patientapp.ui.presentation.auth.SignUpModel
import ke.don.patientapp.ui.presentation.patients.PatientFormModel
import ke.don.patientapp.ui.presentation.patients.PatientListModel
import ke.don.patientapp.ui.presentation.visit.VisitModel
import ke.don.patientapp.ui.presentation.vitals.VitalsModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val authModule = module {
    includes(datasourceModule)
    factoryOf(::SignUpModel)
    factoryOf(::SignInModel)
}

val patientModule = module {
    includes(datasourceModule)
    factoryOf(::PatientListModel)
    factoryOf(::PatientFormModel)
}

val visitModule = module {
    includes(datasourceModule)
    factoryOf(::VisitModel)
}

val vitalsModule = module {
    includes(datasourceModule)
    factoryOf(::VitalsModel)
}