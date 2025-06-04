package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi

// TODO: Remove direct instantiation once DI is set up
// import android.app.Application
// import it.airbagstudio.ticare.pages.patientDetails.form.data.datasource.local.JsonDataSourceImpl
// import it.airbagstudio.ticare.pages.patientDetails.form.data.repository.FormRepositoryImpl
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBISection
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.Caregiver
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CaregiverData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SectionType
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.ZoneOffset
import java.util.Calendar
import kotlin.let
import kotlin.text.split
import kotlin.text.toInt

/**
 * ViewModel per la gestione dello stato del form CBI.
 */
// TODO: Inject FormRepository using Hilt or Koin
// class CbiFormViewModel(application: Application) : AndroidViewModel(application) {
class CbiFormViewModel(
    private val formRepository: FormRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CbiFormUiState>(CbiFormUiState.Editing())
    val uiState: StateFlow<CbiFormUiState> = _uiState.asStateFlow()

    // Field keys for validation state
    object ValidationKeys {
        const val PATIENT_DATA_SECTION = "patient_data_section"
        const val CAREGIVER_DATA_SECTION = "caregiver_data_section"
        const val PATIENT_BIRTH_DATE = "patient_birth_date" // Key for birth date validation
        fun sectionKey(type: SectionType) = "section_${type}"
    }
    
    /**
     * Inizializza il form con un ID esistente o crea un nuovo form.
     *
     * @param formId ID del form da caricare (null per nuovo form)
     */
    fun initForm(formId: String?) {
        if (formId == null) {
            // New form
            val calendar = Calendar.getInstance()
            val user = formRepository.getUserDetails()
            val userBirth = user?.birthday?.split(".")?.let {
                java.time.LocalDate.of(it[2].toInt(), it[1].toInt(), it[0].toInt())
            }?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
            _uiState.value = CbiFormUiState.Editing(
                patientData = PatientData(name = user?.name ?: "", surname = user?.surname ?: "", birthDate = userBirth),
                compilationTimestamp = System.currentTimeMillis(),
                isBirthDateValid = true,
                availableCaregivers = getMockCaregivers()
            )
        } else {
            // Load existing form
            viewModelScope.launch {
                _uiState.value = CbiFormUiState.Loading
                try {
                    val form = formRepository.getCbiFormById(formId)
                    if (form != null) {
                        val mockCaregivers = getMockCaregivers()
                        // Attempt to find the selected caregiver in the mock list or create one from form data
                        val selected = mockCaregivers.find {
                            it.firstName == form.caregiverData.name &&
                            it.lastName == form.caregiverData.surname &&
                            it.relationship == form.caregiverData.relationship &&
                            it.contact == form.caregiverData.contactInfo // Line 73 corrected
                        } ?: if (form.caregiverData.name.isNotBlank()) { // Create if caregiverData is populated
                            Caregiver(
                                firstName = form.caregiverData.name,
                                lastName = form.caregiverData.surname,
                                relationship = form.caregiverData.relationship,
                                contact = form.caregiverData.contactInfo // Line 79 corrected
                                // Note: This created caregiver won't have a matching ID in availableCaregivers
                                // unless it was one of the mocks. This is a simplification.
                            )
                        } else {
                            null
                        }

                        _uiState.value = CbiFormUiState.Editing(
                            formId = form.id,
                            patientData = form.patientData,
                            caregiverData = form.caregiverData,
                            sections = form.sections,
                            totalScore = form.totalScore,
                            compilationTimestamp = form.compilationTimestamp,
                            isValid = true,
                            isBirthDateValid = true,
                            availableCaregivers = mockCaregivers,
                            selectedCaregiver = selected
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
    }

    private fun getMockCaregivers(): List<Caregiver> {
        return listOf(
            Caregiver(firstName = "Mario", lastName = "Rossi", relationship = "Figlio", contact = "3331112233"),
            Caregiver(firstName = "Luisa", lastName = "Verdi", relationship = "Moglie", contact = "3334445566"),
            Caregiver(firstName = "Giovanni", lastName = "Bianchi", relationship = "Amico", contact = "3337778899")
        )
    }
    
    /**
     * Aggiorna i dati del paziente.
     *
     * @param patientData Nuovi dati del paziente
     */
    fun updatePatientData(patientData: PatientData) {
        // This function might be less relevant now for name/surname/birthDate
        // as they are becoming static. However, if other patient data fields are added later,
        // it could be useful. For now, we'll keep it, but its direct impact on
        // the static fields is removed.
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    patientData = patientData, // This will update the patientData object in the state
                    isBirthDateValid = true // Birth date is static, so always valid
                )
            }
        }
    }

    /**
     * Aggiorna la data di nascita del paziente (ora static, so this might be deprecated or used for initial setting).
     * For now, this function is not actively used for UI input.
     * @param timestamp Timestamp della data di nascita selezionata
     */
    fun onBirthDateSelected(timestamp: Long) {
        // This function is largely unused now as birth date is static.
        // Kept for potential future use or if initial setting logic needs it.
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    patientData = currentState.patientData.copy(birthDate = timestamp),
                    isBirthDateValid = true // Static, so always valid
                )
            }
        }
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
    
    /**
     * Aggiorna i dati del caregiver (vecchia implementazione, ora gestita da selezione).
     * Questa funzione potrebbe essere rimossa o adattata se c'è ancora un modo diretto
     * per modificare i dati del caregiver al di fuori del flusso di selezione modale.
     * Per ora, la selezione aggiorna direttamente `selectedCaregiver` e `caregiverData` nel form.
     *
     * @param caregiverData Nuovi dati del caregiver
     */
    fun updateCaregiverData(caregiverData: CaregiverData) {
        // This is now primarily handled by onCaregiverSelected.
        // If direct editing of caregiver fields on the main form was still possible,
        // this would be relevant.
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(caregiverData = caregiverData)
            }
        }
    }

    // --- Caregiver Selection Logic ---

    fun onCaregiverSelectorClick() {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(isCaregiverSelectionModalVisible = true, isAddingCaregiver = false)
            }
        }
    }

    fun onDismissCaregiverSelectionModal() {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    isCaregiverSelectionModalVisible = false,
                    isAddingCaregiver = false,
                    // Clear temporary fields for AddCaregiverForm
                    newCaregiverFirstName = "",
                    newCaregiverLastName = "",
                    newCaregiverRelationship = "",
                    newCaregiverContact = "",
                    newCaregiverFirstNameError = null,
                    newCaregiverLastNameError = null,
                    newCaregiverRelationshipError = null,
                    newCaregiverContactError = null
                )
            }
        }
    }

    fun onAddNewCaregiverClick() {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    isAddingCaregiver = true,
                    // Clear temporary fields for AddCaregiverForm
                    newCaregiverFirstName = "",
                    newCaregiverLastName = "",
                    newCaregiverRelationship = "", // Consider setting a default from dropdown options
                    newCaregiverContact = "",
                    newCaregiverFirstNameError = null,
                    newCaregiverLastNameError = null,
                    newCaregiverRelationshipError = null,
                    newCaregiverContactError = null
                )
            }
        }
    }

    fun onCancelAddCaregiver() {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    isAddingCaregiver = false,
                    // Clear temporary fields
                    newCaregiverFirstName = "",
                    newCaregiverLastName = "",
                    newCaregiverRelationship = "",
                    newCaregiverContact = "",
                    newCaregiverFirstNameError = null,
                    newCaregiverLastNameError = null,
                    newCaregiverRelationshipError = null,
                    newCaregiverContactError = null
                )
            }
        }
    }

    fun onSaveNewCaregiver() {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            var isValid = true
            var firstNameError: String? = null
            var lastNameError: String? = null
            var relationshipError: String? = null
            var contactError: String? = null

            if (currentState.newCaregiverFirstName.isBlank()) {
                firstNameError = "Il nome è obbligatorio" // Add to strings.xml
                isValid = false
            }
            if (currentState.newCaregiverLastName.isBlank()) {
                lastNameError = "Il cognome è obbligatorio" // Add to strings.xml
                isValid = false
            }
            if (currentState.newCaregiverRelationship.isBlank()) { // Assuming relationship is mandatory
                relationshipError = "La relazione è obbligatoria" // Add to strings.xml
                isValid = false
            }
            if (currentState.newCaregiverContact.isBlank()) { // Basic validation, could be more complex
                contactError = "Il contatto è obbligatorio" // Add to strings.xml
                isValid = false
            }
            // TODO: Add more specific contact validation (e.g., phone number format)

            if (isValid) {
                val newCaregiver = Caregiver(
                    firstName = currentState.newCaregiverFirstName,
                    lastName = currentState.newCaregiverLastName,
                    relationship = currentState.newCaregiverRelationship,
                    contact = currentState.newCaregiverContact
                )
                _uiState.update {
                    (it as CbiFormUiState.Editing).copy(
                        availableCaregivers = it.availableCaregivers + newCaregiver,
                        isAddingCaregiver = false,
                        // Clear form fields
                        newCaregiverFirstName = "",
                        newCaregiverLastName = "",
                        newCaregiverRelationship = "",
                        newCaregiverContact = "",
                        newCaregiverFirstNameError = null,
                        newCaregiverLastNameError = null,
                        newCaregiverRelationshipError = null,
                        newCaregiverContactError = null
                    )
                }
            } else {
                _uiState.update {
                    (it as CbiFormUiState.Editing).copy(
                        newCaregiverFirstNameError = firstNameError,
                        newCaregiverLastNameError = lastNameError,
                        newCaregiverRelationshipError = relationshipError,
                        newCaregiverContactError = contactError
                    )
                }
            }
        }
    }

    fun onCaregiverSelected(caregiver: Caregiver) {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            val updatedCaregiverData = currentState.caregiverData.copy(
                name = caregiver.firstName,
                surname = caregiver.lastName,
                relationship = caregiver.relationship,
                contactInfo = caregiver.contact // Line 337 corrected
            )
            _uiState.update {
                (it as CbiFormUiState.Editing).copy(
                    selectedCaregiver = caregiver,
                    isCaregiverSelectionModalVisible = false,
                    caregiverData = updatedCaregiverData // Update the main form's caregiverData
                )
            }
            // After selecting a caregiver, re-validate the caregiver section if needed,
            // or ensure the main form's validation reflects this.
            // For now, we assume selection implies valid data for the main form.
            // If caregiverData fields were previously invalid, selecting a caregiver should clear those specific errors.
            val newInvalidKeys = currentState.invalidFieldKeys.toMutableSet()
            newInvalidKeys.remove(ValidationKeys.CAREGIVER_DATA_SECTION) // Clear caregiver section error
            _uiState.update {
                (it as CbiFormUiState.Editing).copy(invalidFieldKeys = newInvalidKeys)
            }
        }
    }


    // --- Input Change Handlers for AddCaregiverForm ---
    fun onNewCaregiverFirstNameChanged(name: String) {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    newCaregiverFirstName = name,
                    newCaregiverFirstNameError = null // Clear error on change
                )
            }
        }
    }

    fun onNewCaregiverLastNameChanged(surname: String) {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    newCaregiverLastName = surname,
                    newCaregiverLastNameError = null // Clear error on change
                )
            }
        }
    }

    fun onNewCaregiverRelationshipChanged(relationship: String) {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    newCaregiverRelationship = relationship,
                    newCaregiverRelationshipError = null // Clear error on change
                )
            }
        }
    }

    fun onNewCaregiverContactChanged(contact: String) {
        val currentState = _uiState.value
        if (currentState is CbiFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    newCaregiverContact = contact,
                    newCaregiverContactError = null // Clear error on change
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

    /**
     * Salva il form.
     */
    fun saveForm() {
        val currentState = _uiState.value
        if (currentState !is CbiFormUiState.Editing) return

        if (!validateFormAndUpdateState(currentState)) {
            // Il form non è valido, lo stato UI è già stato aggiornato con errori e chiavi invalide.
            // La UI mostrerà la snackbar e gli highlight.
            return
        }
            
        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true, validationErrors = emptyList(), invalidFieldKeys = emptySet()) }
            
            val formToSave = CBIForm(
                id = currentState.formId ?: java.util.UUID.randomUUID().toString(),
                patientData = currentState.patientData, // Patient data (name, surname, birthDate) is static but still part of the form
                caregiverData = currentState.caregiverData,
                sections = currentState.sections,
                totalScore = currentState.totalScore,
                compilationTimestamp = currentState.compilationTimestamp, // Use single timestamp
                lastModified = System.currentTimeMillis()
                // creationDate is handled by CBIForm default or loaded for existing
            )

            try {
                formRepository.saveCbiForm(formToSave)
                _uiState.value = CbiFormUiState.Saved(formToSave)
            } catch (e: IOException) {
                _uiState.value = currentState.copy(
                    isSaving = false,
                    validationErrors = listOf("Errore durante il salvataggio del form: ${e.message}")
                )
            } catch (e: Exception) {
                 _uiState.value = currentState.copy(
                    isSaving = false,
                    validationErrors = listOf("Errore imprevisto durante il salvataggio: ${e.message}")
                )
            }
        }
    }
}
