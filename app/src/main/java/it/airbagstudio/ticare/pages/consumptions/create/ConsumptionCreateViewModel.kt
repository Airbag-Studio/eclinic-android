package it.airbagstudio.ticare.pages.consumptions.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.ClinicType
import ch.ticare.eclinic.library.entity.SaveEmployeeConsumption
import ch.ticare.eclinic.library.repository.ConsumptionRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class ConsumptionCreateUiState(
    val isSuccess: Boolean,
    val title: String,
    val errorMessage: String?,
    val isLoading: Boolean,
    val item: Item,
    val isValid: Boolean
) {
    data class Item(
        val date: Date,
        val quantity: String,
        val notes: String,
        val editable: Boolean
    )
}

@HiltViewModel
class ConsumptionCreateViewModel @Inject constructor(
    private val consumptionRepository: ConsumptionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val isSuccess = MutableStateFlow<Boolean>(false)

    private val articles = consumptionRepository.getArticles()
    private val selectedArticleId = MutableStateFlow<Int?>(null)
    private val consumptionId = MutableStateFlow<Int?>(null)
    private val date = MutableStateFlow<Date>(Date())
    private val quantity = MutableStateFlow<String>("")
    private val notes = MutableStateFlow<String>("")
    private val clinicType = userRepository.getClinicType()

    private val isLoading = MutableStateFlow<Boolean>(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val selectedArticle = combine(articles, selectedArticleId) { _articles, _id ->
        _articles.firstOrNull { it.id == _id }
    }

    private val consumption =
        combine(date, quantity, notes, clinicType) { date, quantity, notes, clinicType ->
            ConsumptionCreateUiState.Item(date, quantity, notes, clinicType == ClinicType.SPITEX)
        }

    val uiState = combine(
        consumption,
        isLoading,
        errorMessage,
        selectedArticle,
        isSuccess
    ) { consumption, isLoading, errorMessage, selectedArticle, isSuccess ->
        val isValid =
            consumption.notes.isNotEmpty() && (consumption.quantity.toDoubleOrNull() != null && consumption.quantity.toDouble() > 0)
        ConsumptionCreateUiState(
            title = selectedArticle?.desc ?: "",
            errorMessage = errorMessage,
            isLoading = isLoading,
            item = consumption,
            isSuccess = isSuccess,
            isValid = isValid
        )
    }.catch {
        errorMessage.value = it.localizedMessage
        isLoading.value = false
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = ConsumptionCreateUiState(
            title = "",
            errorMessage = null,
            isLoading = false,
            item = ConsumptionCreateUiState.Item(Date(), "", "", false),
            isSuccess = false,
            isValid = false
        )
    )

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        errorMessage.value = throwable.localizedMessage
        isLoading.value = false
    }

    fun setSelectedArticleId(id: Int?) {
        this.selectedArticleId.value = id
    }

    fun setConsumptionId(id: Int?) {
        this.consumptionId.value = id
    }

    fun setDate(date: Date) {
        this.date.value = date
    }

    fun setNotes(value: String) {
        this.notes.value = value
    }

    fun setQuantity(value: String) {
        this.quantity.value = value
    }

    fun clearErrors() {
        errorMessage.value = null
    }

    fun clearData() {
        this.isSuccess.value = false
        this.selectedArticleId.value = null
        date.value = Date()
        quantity.value = ""
        notes.value = ""
    }

    fun saveConsumption() {
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading.value = true
            val item = SaveEmployeeConsumption(
                id = consumptionId.value,
                idItem = selectedArticleId.value!!,
                quantity = quantity.value.toDoubleOrNull() ?: 0.0,
                remarks = notes.value,
                date = date.value.format("yyyy.MM.dd")

            )
            if (item.id != null) {
                val res = consumptionRepository.editConsumption(item)
                res.error?.desc?.let {
                    errorMessage.value = it
                } ?: run {
                    isSuccess.value = true
                }
            } else {
                val res = consumptionRepository.addConsumption(item)
                res.error?.desc?.let {
                    errorMessage.value = it
                } ?: run {
                    isSuccess.value = true
                }
            }
            isLoading.value = false
        }
    }
}