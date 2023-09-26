package it.airbagstudio.ticare.pages.patientDetails

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.data.AlertItem
import it.airbagstudio.ticare.navigation.DestinationsArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientDetailsScreenViewModel @Inject constructor(savedStateHandle: SavedStateHandle): ViewModel() {


    var isLoading by mutableStateOf(false)
    var isLoadingActivities by mutableStateOf(false)

    private val patientCod: String? = savedStateHandle[DestinationsArgs.PATIENT_COD]

    var alerts by mutableStateOf<List<AlertItem>>(listOf(
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri "
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Param"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parame"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        )))

    init {
        viewModelScope.launch {
            isLoading = true
            delay(2000L)
            isLoading = false
            delay(1000L)
            isLoadingActivities = true
            delay(2000L)
            isLoadingActivities = false
        }
    }
}