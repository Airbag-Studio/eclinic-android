package it.airbagstudio.ticare.pages.patientDetails

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.data.AlertItem
import it.airbagstudio.ticare.navigation.DestinationsArgs
import javax.inject.Inject

@HiltViewModel
class PatientDetailsScreenViewModel @Inject constructor(savedStateHandle: SavedStateHandle): ViewModel() {


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
        print(Uri.decode(patientCod))
    }
}