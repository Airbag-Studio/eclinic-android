package it.airbagstudio.ticare.pages.vitalParameters.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.repository.AgendaTaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.vitalParameters.list.VitalParametersUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VitalParameterSearchScreenViewModel @Inject constructor(
    private val agendaTaskRepository: AgendaTaskRepository
): ViewModel() {


    //private val isLoading = MutableStateFlow(false)
    val vitalSignTypes = agendaTaskRepository.getVitalSignTypes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf()
    )

}