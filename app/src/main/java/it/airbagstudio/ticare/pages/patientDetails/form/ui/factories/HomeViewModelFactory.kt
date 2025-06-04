package it.airbagstudio.ticare.pages.patientDetails.form.ui.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.home.HomeViewModel

class HomeViewModelFactory(
    private val formRepository: FormRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(formRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
