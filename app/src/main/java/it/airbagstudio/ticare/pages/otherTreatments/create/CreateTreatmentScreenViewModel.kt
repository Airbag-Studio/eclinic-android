package it.airbagstudio.ticare.pages.otherTreatments.create

import android.text.Html
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AddOtherService
import ch.ticare.eclinic.library.entity.Article
import ch.ticare.eclinic.library.entity.EditOtherService
import ch.ticare.eclinic.library.entity.GuarantorType
import ch.ticare.eclinic.library.entity.OtherService
import ch.ticare.eclinic.library.repository.OtherServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getItemDesc
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date
import javax.inject.Inject

data class CreateTreatmentScreenUiState(
    val newTreatment: NewTreatment = NewTreatment(null, Date(), "", null,"1"),
    val guarantorTypes: List<GuarantorType> = listOf(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) {
    data class NewTreatment(
        val article: Article?,
        val date: Date,
        val description: String,
        val guarantorType: GuarantorType?,
        val quantity: String?
    )
}


@HiltViewModel
class CreateTreatmentScreenViewModel @Inject constructor(
    private val otherServiceRepository: OtherServiceRepository,
) : ViewModel() {

    val selectedArticleId = MutableStateFlow<Int?>(null)
    val patientCode = MutableStateFlow<String?>(null)
    var otherService:OtherService? = null

    private val articles = otherServiceRepository.getArticles()
    private val guarantorTypes = otherServiceRepository.getGuarantors()
    private val selectedDate = MutableStateFlow<Date>(Date())
    private val notes = MutableStateFlow("")
    private val quantity = MutableStateFlow<String>("0")
    private val guarantorType = MutableStateFlow<Int>(0)
    private val isLoading = MutableStateFlow(false)
    private val isSuccess = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val selectedArticle = combine(articles, selectedArticleId) { _articles, _id ->
        _articles.firstOrNull { it.id == _id }
    }

    private val selectedGuarantorType = combine(guarantorTypes, guarantorType) { _guarantors, _id ->
        _guarantors.firstOrNull { it.id == _id }
    }

    private val newService = combine(
        selectedArticle,
        selectedGuarantorType,
        selectedDate,
        notes,
        quantity
    ) { _article, _guarantor, _date, _notes, _quantity ->
        CreateTreatmentScreenUiState.NewTreatment(_article, _date, _notes, _guarantor, _quantity)
    }

    val uiState = combine(
        newService,
        guarantorTypes,
        isLoading,
        isSuccess,
        errorMessage
    ) { _newService, _guarantorTypes, _isLoading, _isSuccess, _errorMessage ->
        CreateTreatmentScreenUiState(
            _newService,
            _guarantorTypes,
            _isLoading,
            _isSuccess,
            _errorMessage
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CreateTreatmentScreenUiState(
            newTreatment = CreateTreatmentScreenUiState.NewTreatment(null, Date(), "", null, "1")
        )
    )

    fun setDate(date: Date) {
        selectedDate.value = date
    }

    fun setNotes(value: String) {
        notes.value = value
    }

    fun setQuantity(value: String) {
        quantity.value = value
    }

    fun setGuarantorId(id: Int?) {
        guarantorType.value = id ?: 0
    }

    fun clearState() {
        errorMessage.value = null
        isSuccess.value = false
    }

    fun saveTreatment() {
        if (otherService != null){
            patchService(otherService!!)
        }else{
            createNewService()
        }
    }

    private fun patchService(otherService: OtherService){
        isLoading.value = true
        viewModelScope.launch() {
            val editService = EditOtherService(
                id = otherService.id,
                cod = patientCode.value ?: "",
                dateTime = selectedDate.value.format("yyyy-MM-dd HH:mm:00"),
                item = selectedArticleId.value ?: 0,
                guarantorType = guarantorType.value,
                desc = notes.value,
                quantity = quantity.value.toDoubleOrNull() ?: 0.0
            )
            val res = otherServiceRepository.updateOtherService(listOf(editService))
            if (res.status == "success") {
                isSuccess.value = true
            } else if (res.status == "error") {
                errorMessage.value = res.error?.desc ?: ""
            }
            isLoading.value = false
        }
    }

    private fun createNewService(){
        isLoading.value = true
        viewModelScope.launch() {
            val newService = AddOtherService(
                cod = patientCode.value ?: "",
                dateTime = selectedDate.value.format("yyyy-MM-dd HH:mm:00"),
                item = selectedArticleId.value ?: 0,
                guarantorType = guarantorType.value,
                desc = notes.value,
                quantity = quantity.value.toDoubleOrNull() ?: 0.0

            )

            val res = otherServiceRepository.addOtherServices(listOf(newService))
            if (res.status == "success") {
                isSuccess.value = true
            } else if (res.status == "error") {
                errorMessage.value = res.error?.desc ?: ""
            }
            isLoading.value = false

        }
    }

    fun setService(otherService: OtherService?) {
        this.otherService = otherService
        if (otherService != null) {
            viewModelScope.launch {
                quantity.value = otherService.quantity.toString()
                guarantorType.value = otherService.guarantorID
                selectedArticleId.value = otherService.itemID
                otherService.dateTime.toDate("yyyy-MM-dd'T'HH:mm:00.000")?.let { date ->
                    setDate(date)
                }
                notes.value = otherService.desc
            }
        }
    }

}