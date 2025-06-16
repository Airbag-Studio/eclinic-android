package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi

// TODO: Remove direct instantiation once DI is set up
// import android.app.Application
// import it.airbagstudio.ticare.pages.patientDetails.form.data.datasource.local.JsonDataSourceImpl
// import it.airbagstudio.ticare.pages.patientDetails.form.data.repository.FormRepositoryImpl
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.Contact
import ch.ticare.eclinic.library.entity.form.CbiScale
import ch.ticare.eclinic.library.repository.FormRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBISection
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SectionType
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.ZoneOffset
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel per la gestione dello stato del form CBI.
 */
// TODO: Inject FormRepository using Hilt or Koin
// class CbiFormViewModel(application: Application) : AndroidViewModel(application) {
@HiltViewModel
class CbiFormViewModel @Inject constructor(
    private val formRepository: FormRepository,
    private val userDetailRepository: UserDetailRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CbiFormUiState>(CbiFormUiState.Editing())
    val uiState: StateFlow<CbiFormUiState> = _uiState.asStateFlow()

    private var userId: Int? = null
    private var formId: Int? = null

    // Field keys for validation state
    object ValidationKeys {
        const val PATIENT_DATA_SECTION = "patient_data_section"
        const val CAREGIVER_DATA_SECTION = "caregiver_data_section"
        const val PATIENT_BIRTH_DATE = "patient_birth_date" // Key for birth date validation
        fun sectionKey(type: SectionType) = "section_${type}"
    }

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserID().collect(){
                userId = it
            }
        }

    }
    /**
     * Inizializza il form con un ID esistente o crea un nuovo form.
     *
     * @param formId ID del form da caricare (null per nuovo form)
     */
    fun initForm(formId: String?) {
        this.formId = formId?.toIntOrNull()
        val currentCase = userDetailRepository.getCurrentCase() ?: return
        val patientCode = currentCase.patientCod ?: return
        val userBirth = currentCase.birthday.split(".").let {
            java.time.LocalDate.of(it[2].toInt(), it[1].toInt(), it[0].toInt())
        }?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        val patientData = PatientData(name = currentCase.name, surname = currentCase.surname, birthDate = userBirth)
        viewModelScope.launch {
            val form = formRepository.getCbiScaleList(patientCode).results?.firstOrNull { it.iD == formId?.toInt() }
            _uiState.value = CbiFormUiState.Editing(
                patientData = patientData,
                compilationTimestamp = System.currentTimeMillis(),
                isBirthDateValid = true,
                selectedCaregiver = currentCase.contacts.otherContacts.firstOrNull { it.id == form?.iDContact },
                totalScore = form?.getTotalScore() ?: 0,
            )
            updateQuestionScore(SectionType.OBJECTIVE, 1, form?.tableTRow1 ?: 0)
            updateQuestionScore(SectionType.OBJECTIVE, 2, form?.tableTRow2 ?: 0)
            updateQuestionScore(SectionType.OBJECTIVE, 3, form?.tableTRow3 ?: 0)
            updateQuestionScore(SectionType.OBJECTIVE, 4, form?.tableTRow4 ?: 0)
            updateQuestionScore(SectionType.OBJECTIVE, 5, form?.tableTRow5 ?: 0)
            updateQuestionScore(SectionType.PSYCHOLOGICAL, 6, form?.tableSRow1 ?: 0)
            updateQuestionScore(SectionType.PSYCHOLOGICAL, 7, form?.tableSRow2 ?: 0)
            updateQuestionScore(SectionType.PSYCHOLOGICAL, 8, form?.tableSRow3 ?: 0)
            updateQuestionScore(SectionType.PSYCHOLOGICAL, 9, form?.tableSRow4 ?: 0)
            updateQuestionScore(SectionType.PSYCHOLOGICAL, 10, form?.tableSRow5 ?: 0)
            updateQuestionScore(SectionType.PHYSICAL, 11, form?.tableFRow1 ?: 0)
            updateQuestionScore(SectionType.PHYSICAL, 12, form?.tableFRow2 ?: 0)
            updateQuestionScore(SectionType.PHYSICAL, 13, form?.tableFRow3 ?: 0)
            updateQuestionScore(SectionType.PHYSICAL, 14, form?.tableFRow4 ?: 0)
            updateQuestionScore(SectionType.SOCIAL, 15, form?.tableDRow1 ?: 0)
            updateQuestionScore(SectionType.SOCIAL, 16, form?.tableDRow2 ?: 0)
            updateQuestionScore(SectionType.SOCIAL, 17, form?.tableDRow3 ?: 0)
            updateQuestionScore(SectionType.SOCIAL, 18, form?.tableDRow4 ?: 0)
            updateQuestionScore(SectionType.SOCIAL, 19, form?.tableDRow5 ?: 0)
            updateQuestionScore(SectionType.EMOTIONAL, 20, form?.tableERow1 ?: 0)
            updateQuestionScore(SectionType.EMOTIONAL, 21, form?.tableERow2 ?: 0)
            updateQuestionScore(SectionType.EMOTIONAL, 22, form?.tableERow3 ?: 0)
            updateQuestionScore(SectionType.EMOTIONAL, 23, form?.tableERow4 ?: 0)
            updateQuestionScore(SectionType.EMOTIONAL, 24, form?.tableERow5 ?: 0)

        }
/*
        if (formId == null) {


        } else {
            // Load existing form
            viewModelScope.launch {
                _uiState.value = CbiFormUiState.Loading
                try {
                    val form = formRepository.getCbiScaleList(patientCode).results?.firstOrNull { it.iD == formId.toInt() }
                    if (form != null) {


                        _uiState.value = CbiFormUiState.Editing(
                            formId = form.iD.toString(),
                            patientData = patientData,
                            caregiverData = form.caregiverData,
                            sections = form.sections,
                            totalScore = form.totalScore,
                            compilationTimestamp = form.compilationTimestamp,
                            isValid = true,
                            isBirthDateValid = true
                        )
                    } else {
                        _uiState.value = CbiFormUiState.Error("Form non trovato.")
                    }
                } catch (e: IOException) {
                    _uiState.value = CbiFormUiState.Error("Errore nel caricamento del form: ${e.message}")
                } catch (e: Exception) {
                    _uiState.value = CbiFormUiState.Error("Errore imprevisto nel caricamento del form: ${e.message}")
                }
            }
        }

 */
    }

    /**
     * Aggiorna il timestamp di data e ora di compilazione selezionato.
     *
     * @param timestamp Timestamp della data e ora di compilazione
     */
    fun onCompilationDateTimeSelected(timestamp: Long) {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    compilationTimestamp = timestamp,
                    isCompilationTimestampValid = true // Assume valid once selected
                )
            }
        }
    }

    // --- End Caregiver Selection Logic ---
    
    /**
     * Aggiorna il punteggio di una domanda in una sezione specifica.
     *
     * @param sectionType Tipo di sezione
     * @param questionId ID della domanda
     * @param score Nuovo punteggio
     */
    fun updateQuestionScore(sectionType: SectionType, questionId: Int, score: Int) {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            // Trova l'indice della sezione
            val sectionIndex = currentState.sections.indexOfFirst { it.type == sectionType }
            if (sectionIndex == -1) return
            
            // Crea una copia delle sezioni
            val updatedSections = currentState.sections.toMutableList()
            
            // Ottieni la sezione corrente
            val currentSection = updatedSections[sectionIndex]
            
            // Trova l'indice della domanda
            val questionIndex = currentSection.questions.indexOfFirst { it.questionId == questionId }
            if (questionIndex == -1) return
            
            // Aggiorna il punteggio della domanda
            val updatedQuestions = currentSection.questions.toMutableList()
            updatedQuestions[questionIndex] = updatedQuestions[questionIndex].copy(score = score)
            
            // Calcola il nuovo punteggio parziale
            val partialScore = calculatePartialScore(updatedQuestions)
            
            // Aggiorna la sezione
            updatedSections[sectionIndex] = currentSection.copy(
                questions = updatedQuestions,
                partialScore = partialScore
            )
            
            // Calcola il nuovo punteggio totale
            val totalScore = calculateTotalScore(updatedSections)
            
            // Aggiorna lo stato
            _uiState.update {
                (it as CbiFormUiState.Editing).copy(
                    sections = updatedSections,
                    totalScore = totalScore
                )
            }
            // Passive validation: validateForm() // Removed: Validate only on save attempt
        }
    }
    
    /**
     * Calcola il punteggio parziale di una sezione.
     *
     * @param questions Lista delle domande della sezione
     * @return Punteggio parziale
     */
    private fun calculatePartialScore(questions: List<QuestionResponse>): Int {
        return questions.sumOf { it.score ?: 0 } // Handle nullable score
    }
    
    /**
     * Calcola il punteggio totale del form.
     *
     * @param sections Lista delle sezioni del form
     * @return Punteggio totale
     */
    private fun calculateTotalScore(sections: List<CBISection>): Int {
        var totalScore = 0
        
        sections.forEach { section ->
            // Applica il fattore di correzione al punteggio parziale
            val correctedScore = (section.partialScore * section.correctionFactor).toInt()
            totalScore += correctedScore
        }
        
        return totalScore
    }
    
    /**
     * Valida il form e aggiorna lo stato di validità.
     * Popola invalidFieldKeys e un messaggio di errore riassuntivo.
     * @return true se il form è valido, false altrimenti.
     */
    private fun validateFormAndUpdateState(editingState: CbiFormUiState.Editing): Boolean {
        val invalidKeys = mutableSetOf<String>()
        var missingFieldsCount = 0

        // Verifica dati paziente - Name, Surname, BirthDate are now static.
        // Compilation timestamp is assumed valid if selected.

        // Verifica dati caregiver (selectedCaregiver ensures these are populated if one is selected)
        // If no caregiver is selected, it's an invalid state for saving.
        if (editingState.selectedCaregiver == null) {
            // This implies caregiverData in the main form is also likely empty or incomplete.
            invalidKeys.add(ValidationKeys.CAREGIVER_DATA_SECTION)
            missingFieldsCount++ // Count as one missing "field" - the caregiver selection itself
        } else {
            // If a caregiver is selected, its details are copied to caregiverData.
            // We assume these details are inherently valid as they come from a Caregiver object.
            // If caregiverData itself had separate validation rules beyond simple presence,
            // they would be checked here based on editingState.caregiverData.
            // For now, selecting a caregiver makes this section valid.
        }

        // Verifica che tutte le domande abbiano una risposta
        editingState.sections.forEach { section ->
            var sectionHasUnansweredQuestion = false
            for (question in section.questions) {
                if (question.score == null) {
                    sectionHasUnansweredQuestion = true
                    missingFieldsCount++ // Increment for each unanswered question
                }
            }
            if (sectionHasUnansweredQuestion) {
                invalidKeys.add(ValidationKeys.sectionKey(section.type))
            }
        }

        val validationSummaryError = if (missingFieldsCount > 0) {
            "Compilazione incompleta: mancano $missingFieldsCount campi obbligatori."
        } else {
            ""
        }

        _uiState.update {
            // Patient name, surname, birthDate are static, so isBirthDateValid is always true from UI perspective.
            // If PatientData model itself had validation, that would be separate.
            (it as CbiFormUiState.Editing).copy(
                isValid = invalidKeys.isEmpty(),
                isBirthDateValid = true, // Static, so always valid from input perspective
                // Update validation for compilation date/time if rules are added
                // isCompilationDateValid = ...,
                // isCompilationTimeValid = ...,
                validationErrors = if (validationSummaryError.isNotBlank()) listOf(validationSummaryError) else emptyList(),
                invalidFieldKeys = invalidKeys
            )
        }
        return invalidKeys.isEmpty()
    }

    fun selectCaregiver(caregiver: Contact) {
        _uiState.update {
            (it as CbiFormUiState.Editing).copy(
                selectedCaregiver = caregiver,
                isCaregiverSelectionModalVisible = false
            )
        }
    }

    /**
     * Salva il form.
     */
    fun saveForm() {
        val patientCode = userDetailRepository.getCurrentCase()?.patientCod ?: return
        val userId = userId ?: return
        val currentState = _uiState.value
        if (currentState !is CbiFormUiState.Editing) return

        if (!validateFormAndUpdateState(currentState)) {
            // Il form non è valido, lo stato UI è già stato aggiornato con errori e chiavi invalide.
            // La UI mostrerà la snackbar e gli highlight.
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true, validationErrors = emptyList(), invalidFieldKeys = emptySet()) }
            val formToSave = CbiScale(
                id = formId,
                cODCase = patientCode,
                iDUser = userId,
                iDContact = currentState.selectedCaregiver?.id ?: 0,
                evalDateTime = Date(currentState.compilationTimestamp).format(SERVER_PARAMETER_DATE_TIME_FORMAT),
                tableTRow1 = currentState.sections[0].questions[0].score ?: -1,
                tableTRow2 = currentState.sections[0].questions[1].score ?: -1,
                tableTRow3 = currentState.sections[0].questions[2].score ?: -1,
                tableTRow4 = currentState.sections[0].questions[3].score ?: -1,
                tableTRow5 = currentState.sections[0].questions[4].score ?: -1,
                tableSRow1 = currentState.sections[1].questions[0].score ?: -1,
                tableSRow2 = currentState.sections[1].questions[1].score ?: -1,
                tableSRow3 = currentState.sections[1].questions[2].score ?: -1,
                tableSRow4 = currentState.sections[1].questions[3].score ?: -1,
                tableSRow5 = currentState.sections[1].questions[4].score ?: -1,
                tableFRow1 = currentState.sections[2].questions[0].score ?: -1,
                tableFRow2 = currentState.sections[2].questions[1].score ?: -1,
                tableFRow3 = currentState.sections[2].questions[2].score ?: -1,
                tableFRow4 = currentState.sections[2].questions[3].score ?: -1,
                tableDRow1 = currentState.sections[3].questions[0].score ?: -1,
                tableDRow2 = currentState.sections[3].questions[1].score ?: -1,
                tableDRow3 = currentState.sections[3].questions[2].score ?: -1,
                tableDRow4 = currentState.sections[3].questions[3].score ?: -1,
                tableDRow5 = currentState.sections[3].questions[4].score ?: -1,
                tableERow1 = currentState.sections[4].questions[0].score ?: -1,
                tableERow2 = currentState.sections[4].questions[1].score ?: -1,
                tableERow3 = currentState.sections[4].questions[2].score ?: -1,
                tableERow4 = currentState.sections[4].questions[3].score ?: -1,
                tableERow5 = currentState.sections[4].questions[4].score ?: -1,
            )
            try {
                if (formId == null) {
                    val res = formRepository.addCbiScale(formToSave)
                    res.results?.firstOrNull()?.id?.let {
                        _uiState.value = CbiFormUiState.Saved(it)

                    }
                }else{
                    val res = formRepository.editCbiScale(formToSave)
                    res.results?.firstOrNull()?.id?.let {
                        _uiState.value = CbiFormUiState.Saved(it)

                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _uiState.value = currentState.copy(
                    isSaving = false,
                    validationErrors = listOf("Errore durante il salvataggio del form: ${e.message}")
                )
            } catch (e: Exception) {
                e.printStackTrace()
                 _uiState.value = currentState.copy(
                    isSaving = false,
                    validationErrors = listOf("Errore imprevisto durante il salvataggio: ${e.message}")
                )
            }
        }
    }
}
