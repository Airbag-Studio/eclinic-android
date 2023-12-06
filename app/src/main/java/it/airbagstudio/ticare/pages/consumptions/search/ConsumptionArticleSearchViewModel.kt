package it.airbagstudio.ticare.pages.consumptions.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.Article
import ch.ticare.eclinic.library.repository.ConsumptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.otherTreatments.OtherTreatmentItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ConsumptionArticleSearchUiState(
    val articles: List<Article> = listOf(),
    val isLoading: Boolean,
    val searchQuery: String = ""
)

@HiltViewModel
class ConsumptionArticleSearchViewModel @Inject constructor(
    private val consumptionRepository: ConsumptionRepository
): ViewModel() {

    private var query = MutableStateFlow("")

    private val articles = consumptionRepository.getArticles()


    val uiState = combine(articles,query){ _articles,_query ->
        var articleModels = _articles
        var filteredArticles = listOf<Article>()
        if (_query.count() > 2){
            filteredArticles = articleModels.filter { it.desc.contains(_query,true) || it.code.contains(_query,true) }
        }else{
            filteredArticles = articleModels
        }

        ConsumptionArticleSearchUiState(
            articles = filteredArticles,
            isLoading = false,
            searchQuery = _query
        )

    }.stateIn(
        scope = viewModelScope,
        started =  SharingStarted.WhileSubscribed(5000),
        initialValue = ConsumptionArticleSearchUiState(
            isLoading = true
        )
    )

    fun setSearchQuery(value: String){
        query.value = value
    }


}