package it.airbagstudio.ticare.pages.patientDetails.form.ui.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos3gg.IPOS3ggFormViewModel

class IPOS3ggFormViewModelFactory(
    private val formRepository: FormRepository,
    private val formId: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IPOS3ggFormViewModel::class.java)) {
            return IPOS3ggFormViewModel(formRepository, formId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for IPOS3ggFormViewModelFactory")
    }
}
