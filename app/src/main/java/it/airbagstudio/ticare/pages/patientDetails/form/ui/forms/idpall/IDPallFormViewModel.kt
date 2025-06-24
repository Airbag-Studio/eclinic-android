package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.idpall

import android.opengl.ETC1.isValid
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.form.IDPallTestScale
import ch.ticare.eclinic.library.repository.FormRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserRepository
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IDPallForm
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IDPallQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos.IPOSFormUiState
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel per la gestione del form ID PALL.
 * Gestisce lo stato del form, la validazione dei dati e le operazioni di salvataggio.
 */
@HiltViewModel
class IDPallFormViewModel @Inject constructor(
    private val repository: FormRepository,
    private val userDetailRepository: UserDetailRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<IDPallFormUiState>(IDPallFormUiState.Loading)
    val uiState: StateFlow<IDPallFormUiState> = _uiState.asStateFlow()
    private var userId: Int? = null

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserID().collect(){
                userId = it
            }
        }
    }

    /**
     * Inizializza il form caricando un form esistente o creandone uno nuovo.
     */
    fun initializeForm(formId: Int?) {
        createNewForm(formId = formId)

    }

    /**
     * Crea un nuovo form ID PALL con le sezioni e domande predefinite.
     */
    private fun createNewForm(formId: Int?) {
        val caseDetails = userDetailRepository.getCurrentCase()
        val codCase = caseDetails?.patientCod ?: return
        val userBirth = caseDetails.birthday.split(".").let {
            java.time.LocalDate.of(it[2].toInt(), it[1].toInt(), it[0].toInt())
        }?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        val patientData = PatientData(name = caseDetails.name, surname = caseDetails.surname, birthDate = userBirth)
        val form = IDPallForm(
            id = UUID.randomUUID().toString(),
            sections = IDPallQuestions.getInitialSections(),
            patientData = PatientData(
                name = patientData.name,
                surname = patientData.surname,
                birthDate = userBirth
            )
        )
        _uiState.value = IDPallFormUiState.Editing(
            patientData = patientData,
            form = form
        )
        viewModelScope.launch {
            repository.getIDPallTestScaleList(codCase).results?.firstOrNull { it.iD == formId }?.let { form ->
                updateQuestionResponse(1, form.tableARow1)
                updateQuestionResponse(2, form.tableARow2QstA)
                updateQuestionResponse(21, form.tableARow2QstB)
                updateQuestionResponse(22, form.tableARow2QstC)
                updateQuestionResponse(23, form.tableARow2QstD)
                updateQuestionResponse(24, form.tableARow2QstD)
                updateQuestionResponse(3, form.tableARow3)
                updateQuestionResponse(4, form.tableARow4)
                updateQuestionResponse(101, form.tableBRow1)
                updateQuestionResponse(102, form.tableBRow2)
                updateQuestionResponse(103, form.tableBRow3)
                updateQuestionResponse(104, form.tableBRow4)
                updateQuestionResponse(105, form.tableBRow5)
                updateQuestionResponse(106, form.tableBRow6)
                updateQuestionResponse(107, form.tableBRow7)
                updateQuestionResponse(108, form.tableBRow8)
            }

        }
    }

    /**
     * Aggiorna i dati del paziente.
     *
     * @param patientData I nuovi dati del paziente.
     */
    fun updatePatientData(patientData: PatientData) {
        val currentState = _uiState.value as? IDPallFormUiState.Editing ?: return
        
        val updatedForm = currentState.form.copy(
            patientData = patientData,
            lastModified = System.currentTimeMillis()
        )
        
        _uiState.update { 
            currentState.copy(
                form = updatedForm,
                patientData = patientData
            )
        }
        validateFormAndUpdateState()

    }

    /**
     * Aggiorna la risposta a una domanda specifica.
     *
     * @param questionId L'ID della domanda da aggiornare.
     * @param isYes True se la risposta è "Sì", false se è "No".
     */
    fun updateQuestionResponse(questionId: Int, isYes: Boolean) {
        val currentState = _uiState.value as? IDPallFormUiState.Editing ?: return
        
        val updatedSections = currentState.form.sections.map { section ->
            section.copy(
                questions = section.questions.map { question ->
                    if (question.questionId == questionId) {
                        question.copy(score = if (isYes) 1 else 0)
                    } else {
                        question
                    }
                }
            )
        }
        
        val updatedForm = currentState.form.copy(
            sections = updatedSections,
            lastModified = System.currentTimeMillis()
        )
        
        _uiState.update { 
            currentState.copy(form = updatedForm)
        }
        validateFormAndUpdateState()
    }

    /**
     * Determina se la sezione "ID PALL Specializzate" è richiesta (obbligatoria).
     * La sezione è obbligatoria se:
     * - Risposta alla domanda 1 è "NO" (score = 0), OPPURE
     * - Risposta è "YES" (score = 1) a una delle domande: 21, 22, 23, 24, 3, 4
     */
    fun isSpecializedSectionRequired(): Boolean {
        val currentState = _uiState.value as? IDPallFormUiState.Editing ?: return false
        val generalSectionQuestions = currentState.form.sections.firstOrNull()?.questions ?: return false
        
        // Trova la risposta alla domanda 1
        val question1Response = generalSectionQuestions.find { it.questionId == 1 }?.score
        
        // Se la risposta alla domanda 1 è "NO" (score = 0), la sezione specializzata è obbligatoria
        if (question1Response == 0) {
            return true
        }
        
        // Altrimenti, controlla se c'è almeno una risposta "YES" alle domande trigger
        val triggerQuestionIds = setOf(21, 22, 23, 24, 3, 4)
        val hasYesResponseInTriggerQuestions = generalSectionQuestions
            .filter { it.questionId in triggerQuestionIds }
            .any { it.score == 1 }
        
        return hasYesResponseInTriggerQuestions
    }

    /**
     * Valida il form e aggiorna lo stato di validazione.
     */
    private fun validateFormAndUpdateState() {
        val currentState = _uiState.value as? IDPallFormUiState.Editing ?: return
        
        var isValid = true
        
        // Valida la sezione generale (esclusa la domanda 2 che è informativa)
        val generalQuestions = currentState.form.sections[0].questions.filter { it.questionId != 2 }
        if (generalQuestions.any { it.score == null }) {
            isValid = false
        }
        
        // Valida la sezione specializzata solo se è richiesta
        if (isSpecializedSectionRequired()) {
            val specializedQuestions = currentState.form.sections.getOrNull(1)?.questions ?: emptyList()
            if (specializedQuestions.any { it.score == null }) {
                isValid = false
            }
        }
        
        _uiState.update {
            currentState.copy(isFormValid = isValid)
        }
    }

    fun onCompilationDateTimeSelected(timestamp: Long) {
        val currentState = _uiState.value
        if (currentState is IDPallFormUiState.Editing) {
            val updatedState = currentState.copy(
                form = currentState.form.copy(compilationTimestamp = timestamp)
            )
            _uiState.update { updatedState }
            validateFormAndUpdateState()
        }
    }

    /**
     * Salva il form corrente.
     */
    fun saveForm(formId: Int?) {
        val currentState = _uiState.value as? IDPallFormUiState.Editing ?: return
        val currentCase = userDetailRepository.getCurrentCase() ?: return
        val caseCode = currentCase.patientCod ?: return
        val userId = userId ?: return
        // Validazione prima del salvataggio
        if (currentState.patientData.birthDate == null) {
            _uiState.update { 
                currentState.copy(isBirthDateValid = false)
            }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { 
                    currentState.copy(isSaving = true)
                }
                
                val formToSave = currentState.form.copy(
                    patientData = currentState.patientData,
                    lastModified = System.currentTimeMillis(),
                    compilationTimestamp = System.currentTimeMillis()
                )
                
                // Ottieni le risposte dalla sezione specializzata (sempre presenti dato che la sezione è sempre mostrata)
                val specializedSectionResponses = formToSave.sections.getOrNull(1)?.questions ?: emptyList()
                
                val toSend = IDPallTestScale(
                    iD = formId,
                    cODCase = caseCode,
                    iDUser = userId,
                    evalDateTime = Date(currentState.form.compilationTimestamp).format(SERVER_PARAMETER_DATE_TIME_FORMAT),
                    nameSurnameUser = "${currentState.patientData.name} ${currentState.patientData.surname}",
                    tableARow1 = formToSave.sections[0].questions[0].score == 1,
                    tableARow2QstA = formToSave.sections[0].questions[1].score == 1,
                    tableARow2QstB = formToSave.sections[0].questions[2].score == 1,
                    tableARow2QstC = formToSave.sections[0].questions[3].score == 1,
                    tableARow2QstD = formToSave.sections[0].questions[4].score == 1,
                    tableARow3 = formToSave.sections[0].questions[5].score == 1,
                    tableARow4 = formToSave.sections[0].questions[6].score == 1,
                    tableBRow1 = (specializedSectionResponses.getOrNull(0)?.score ?: 0) == 1,
                    tableBRow2 = (specializedSectionResponses.getOrNull(1)?.score ?: 0) == 1,
                    tableBRow3 = (specializedSectionResponses.getOrNull(2)?.score ?: 0) == 1,
                    tableBRow4 = (specializedSectionResponses.getOrNull(3)?.score ?: 0) == 1,
                    tableBRow5 = (specializedSectionResponses.getOrNull(4)?.score ?: 0) == 1,
                    tableBRow6 = (specializedSectionResponses.getOrNull(5)?.score ?: 0) == 1,
                    tableBRow7 = (specializedSectionResponses.getOrNull(6)?.score ?: 0) == 1,
                    tableBRow8 = (specializedSectionResponses.getOrNull(7)?.score ?: 0) == 1
                    )
                if (formId != null) {
                    repository.editIDPallTestScale(toSend)
                }else{
                    repository.addIDPallTestScale(toSend)
                }
                _uiState.value = IDPallFormUiState.Saved(formToSave)
            } catch (e: Exception) {
                _uiState.value = IDPallFormUiState.Error("Errore durante il salvataggio: ${e.message}")
            }
        }
    }

    /**
     * Resetta lo stato di errore e torna alla modalità di modifica.
     */
    fun clearError() {
        val currentState = _uiState.value
        if (currentState is IDPallFormUiState.Error) {

        }
    }

    /**
     * Resetta lo stato di salvataggio e torna alla modalità di modifica.
     */
    fun clearSavedState() {
        val currentState = _uiState.value
        if (currentState is IDPallFormUiState.Saved) {
            _uiState.value = IDPallFormUiState.Editing(
                form = currentState.savedForm,
                patientData = currentState.savedForm.patientData
            )
        }
    }
}
