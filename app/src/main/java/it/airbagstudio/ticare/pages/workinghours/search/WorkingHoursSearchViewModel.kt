package it.airbagstudio.ticare.pages.workinghours.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.WorkingHoursType
import ch.ticare.eclinic.library.repository.WorkingHourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class WorkingHoursSearchUiState(
    val types: List<WorkingHoursType> = listOf(),
    val isLoading: Boolean,
    val searchQuery: String = ""
)

@HiltViewModel
class WorkingHoursSearchViewModel @Inject constructor(
    private val workingHourRepository: WorkingHourRepository
): ViewModel() {

    private var query = MutableStateFlow("")

    private val types = workingHourRepository.getTypes()


    val uiState = combine(types,query){ _types, _query ->
        Log.d("types",_types.toString())
        var typeModels = _types
        var filteredTypes = listOf<WorkingHoursType>()
        if (_query.count() > 2){
            filteredTypes = typeModels.filter { it.name.contains(_query,true) }
        }else{
            filteredTypes = typeModels
        }
        WorkingHoursSearchUiState(
            types = filteredTypes,
            isLoading = false,
            searchQuery = _query
        )
    }.stateIn(
        scope = viewModelScope,
        started =  SharingStarted.WhileSubscribed(5000),
        initialValue = WorkingHoursSearchUiState(
            isLoading = true
        )
    )

    fun setSearchQuery(value: String){
        query.value = value
    }


}