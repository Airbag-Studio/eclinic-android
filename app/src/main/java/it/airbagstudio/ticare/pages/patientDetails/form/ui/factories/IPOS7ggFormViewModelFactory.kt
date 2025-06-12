package it.airbagstudio.ticare.pages.patientDetails.form.ui.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos7gg.IPOS7ggFormViewModel // Import changed

class IPOS7ggFormViewModelFactory( // Class name changed
    private val oldFormRepository: OldFormRepository,
    private val formId: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IPOS7ggFormViewModel::class.java)) { // Class check changed
            return IPOS7ggFormViewModel(oldFormRepository, formId) as T // Instantiation changed
        }
        throw IllegalArgumentException("Unknown ViewModel class for IPOS7ggFormViewModelFactory") // Message changed
    }
}
