package it.airbagstudio.ticare.pages.drugsAdministration

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import javax.inject.Inject

@HiltViewModel
class DrugsAdministrationScreenViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val patientCod: String? = savedStateHandle[DestinationsArgs.PATIENT_COD]

    //val reserves by mutableStateOf()

}