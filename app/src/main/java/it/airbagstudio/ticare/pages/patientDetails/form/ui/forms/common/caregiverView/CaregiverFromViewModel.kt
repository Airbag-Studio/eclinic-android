package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.common.caregiverView

import android.R.attr.name
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.Contact
import ch.ticare.eclinic.library.entity.ContactCreate
import ch.ticare.eclinic.library.entity.Relationship
import ch.ticare.eclinic.library.repository.FormRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import coil.util.CoilUtils.result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

data class ContactCreateUiState(
    val name: String = "",
    val surname: String = "",
    val telephone: String = "",
    val idRelationship: Int = -1,

    var isValid: Boolean = {
        name.isNotEmpty() && surname.isNotEmpty() && telephone.isNotEmpty() && idRelationship >= 0
    }()
)

@HiltViewModel
class CaregiverFromViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val formRepository: FormRepository
): ViewModel() {

    private val _contactCreateUiState = MutableStateFlow(ContactCreateUiState())
    val contactCreateUiState = _contactCreateUiState.asStateFlow()

    private val _caregivers = MutableStateFlow<List<Contact>>(emptyList())
    val caregivers = _caregivers.asStateFlow()

    private val _relationships = MutableStateFlow<List<Relationship>>(emptyList())
    val relationships = _relationships.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private var patientCode: String? = null

    init {
        fetchContacts()
        fetchRelationships()
    }

    fun fetchContacts(){
        userDetailRepository.getCurrentCase()?.patientCod?.let { patientCod ->
            this.patientCode = patientCod
            viewModelScope.launch(coroutineExceptionHandler) {
                val caseDetails = userDetailRepository.getCase(patientCod)
                _caregivers.value = caseDetails.results?.firstOrNull()?.contacts?.otherContacts ?: listOf()
            }
        }

    }

    fun fetchRelationships(){
        viewModelScope.launch(coroutineExceptionHandler) {
            formRepository.getRelationShip().collect {
                _relationships.value = it
            }
        }
    }

    //Contact creation
    fun isContactValid(){
        val isValid = _contactCreateUiState.value.name.isNotEmpty() && _contactCreateUiState.value.surname.isNotEmpty() && _contactCreateUiState.value.telephone.isNotEmpty() && _contactCreateUiState.value.idRelationship >= 0
        _contactCreateUiState.value = _contactCreateUiState.value.copy(isValid = isValid)
    }

    fun setContactName(value: String){
        _contactCreateUiState.value = _contactCreateUiState.value.copy(name = value)
        isContactValid()
    }

    fun setContactSurname(value: String){
        _contactCreateUiState.value = _contactCreateUiState.value.copy(surname = value)
        isContactValid()
    }

    fun setContactTelephone(value: String){
        _contactCreateUiState.value = _contactCreateUiState.value.copy(telephone = value)
        isContactValid()
    }

    fun setContactRelationship(value: Int){
        _contactCreateUiState.value = _contactCreateUiState.value.copy(idRelationship = value)
        isContactValid()
    }

    fun saveNewCaregiver(){
        val patientCode = patientCode ?: return
        val newCaregiver = ContactCreate(
            cODCase = patientCode,
            name = _contactCreateUiState.value.name,
            surname = _contactCreateUiState.value.surname,
            telephone = _contactCreateUiState.value.telephone,
            iDRelationship = _contactCreateUiState.value.idRelationship
        )
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = formRepository.saveNewContact(newCaregiver)
            val newContactId = res.results?.firstOrNull()?.id
            fetchContacts()
        }
    }



}