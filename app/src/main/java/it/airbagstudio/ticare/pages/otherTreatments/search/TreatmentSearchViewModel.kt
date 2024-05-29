package it.airbagstudio.ticare.pages.otherTreatments.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.repository.OtherServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.otherTreatments.OtherTreatmentItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TreatmentSearchUiState(
    val articles: List<OtherTreatmentItem> = listOf(),
    val isLoading: Boolean,
    val searchQuery: String = ""
)

@HiltViewModel
class TreatmentSearchViewModel @Inject constructor(
    private val otherServiceRepository: OtherServiceRepository
): ViewModel() {

    private var query = MutableStateFlow("")

    private val articles = otherServiceRepository.getArticles()


    val uiState = combine(articles,query){ _articles,_query ->
        var articleModels = _articles.map { OtherTreatmentItem(name = it.group, description = it.desc, number = it.code,id = it.id, hasDataToUpload = false) }
        var filteredArticles : List<OtherTreatmentItem>
        if (_query.count() > 2){
            filteredArticles = articleModels.filter { it.description.contains(_query,true) || it.name.contains(_query,true) }
        }else{
            filteredArticles = articleModels
        }

        TreatmentSearchUiState(
            articles = filteredArticles,
            isLoading = false,
            searchQuery = _query
        )

    }.stateIn(
        scope = viewModelScope,
        started =  SharingStarted.WhileSubscribed(5000),
        initialValue = TreatmentSearchUiState(
            isLoading = true
        )
    )

    fun setSearchQuery(value: String){
        query.value = value
    }


}