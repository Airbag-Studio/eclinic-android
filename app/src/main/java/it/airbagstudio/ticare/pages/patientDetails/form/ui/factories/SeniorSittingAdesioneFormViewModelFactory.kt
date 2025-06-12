package it.airbagstudio.ticare.pages.patientDetails.form.ui.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsittingadesione.SeniorSittingAdesioneFormViewModel

class SeniorSittingAdesioneFormViewModelFactory(
    private val oldFormRepository: OldFormRepository,
    private val formId: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeniorSittingAdesioneFormViewModel::class.java)) {
            return SeniorSittingAdesioneFormViewModel(oldFormRepository, formId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for SeniorSittingAdesioneFormViewModelFactory")
    }
}
