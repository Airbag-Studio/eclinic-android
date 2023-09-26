package it.airbagstudio.ticare.pages.drugsAdministration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrugsAdministrationScreenViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    var isLoading by mutableStateOf(false)
    private val patientCod: String? = savedStateHandle[DestinationsArgs.PATIENT_COD]

    init {
        viewModelScope.launch {
            isLoading = true
            delay(2000)
            isLoading = false
        }
    }

}