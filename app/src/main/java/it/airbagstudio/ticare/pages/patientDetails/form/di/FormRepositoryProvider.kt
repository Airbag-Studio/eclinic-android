package it.airbagstudio.ticare.pages.patientDetails.form.di

import android.content.Context
import ch.ticare.eclinic.library.repository.UserDetailRepository
import it.airbagstudio.ticare.pages.patientDetails.form.data.datasource.local.JsonDataSourceImpl
import it.airbagstudio.ticare.pages.patientDetails.form.data.repository.OldFormRepositoryImpl
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository

interface FormRepositoryProvider {
    val oldFormRepository: OldFormRepository
}

fun provideFormRepository(context: Context,userDetailRepository: UserDetailRepository): OldFormRepository {
    val jsonDataSource = JsonDataSourceImpl(context.applicationContext)
    return OldFormRepositoryImpl(userDetailRepository,jsonDataSource)
}
