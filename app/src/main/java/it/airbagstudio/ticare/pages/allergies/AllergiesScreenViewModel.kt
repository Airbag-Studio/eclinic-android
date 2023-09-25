package it.airbagstudio.ticare.pages.allergies

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import javax.inject.Inject

@HiltViewModel
class AllergiesScreenViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val patientCod: String? = savedStateHandle[DestinationsArgs.PATIENT_COD]

    var allergies by mutableStateOf(listOf(
        AllergiesItem(name = "Pennicilina", isDrug = true),
        AllergiesItem(name = "Acido acetilsalicilico ed altri FANS", isDrug = true),
        AllergiesItem(name = "Arachidi", isDrug = false),
    ))
}