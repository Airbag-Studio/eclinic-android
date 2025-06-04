package it.airbagstudio.ticare.pages.patientDetails.form.di

import android.content.Context
import ch.ticare.eclinic.library.repository.UserDetailRepository
import it.airbagstudio.ticare.pages.patientDetails.form.data.datasource.local.JsonDataSourceImpl
import it.airbagstudio.ticare.pages.patientDetails.form.data.repository.FormRepositoryImpl
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository

interface FormRepositoryProvider {
    val formRepository: FormRepository
}

fun provideFormRepository(context: Context,userDetailRepository: UserDetailRepository): FormRepository {
    val jsonDataSource = JsonDataSourceImpl(context.applicationContext)
    return FormRepositoryImpl(userDetailRepository,jsonDataSource)
}
