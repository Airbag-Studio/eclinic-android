package it.airbagstudio.ticare.pages.patientsList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.ui.components.PatientListItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientListScreenViewModel @Inject constructor(): ViewModel() {

    var isLoading by mutableStateOf(true)

    var query by mutableStateOf("")
    var patients by mutableStateOf(listOf(PatientListItem(
        surname = "ABETE",
        name = "Maria",
        address = "Via la Montagna 16",
        cAP = "6962",
        cOD = "23/2172",
        locality = "Viganello",
        birthday = "03.08.1936",
        age = 87,
        photo = "https://www.tag24.it/wp-content/uploads/2023/04/WhatsApp-Image-2023-03-31-at-14.12.21-e1680618678712-800x560.jpeg"
    )))

    init {
        viewModelScope.launch {
            delay(2000L)
            isLoading = false
        }
    }
}