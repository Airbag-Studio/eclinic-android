package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi

import ch.ticare.eclinic.library.entity.Contact
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.CbiQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBISection
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.Caregiver
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CaregiverData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SectionType

/**
 * Rappresenta i possibili stati della UI del form CBI.
 */
sealed class CbiFormUiState {
    /**
     * Stato di caricamento.
     */
    object Loading : CbiFormUiState()
    
    /**
     * Stato di errore.
     *
     * @property message Messaggio di errore
     */
    data class Error(val message: String) : CbiFormUiState()
    
    /**
     * Stato di modifica del form.
     *
     * @property formId ID del form (null se nuovo)
     * @property patientData Dati del paziente
     * @property caregiverData Dati del caregiver
     * @property sections Sezioni del form con le risposte
     * @property totalScore Punteggio totale del form
     * @property isSaving Flag che indica se il salvataggio è in corso
     * @property isValid Flag che indica se il form è valido per il salvataggio
     * @property validationErrors Lista di errori di validazione
     */
    data class Editing(
        val formId: String? = null,
        val patientData: PatientData = PatientData(),
        val caregiverData: CaregiverData = CaregiverData(),
        val sections: List<CBISection> = initializeSections(),
        val totalScore: Int = 0,
        val isSaving: Boolean = false,
        // isValid might be determined by validationErrors.isEmpty() && invalidFieldKeys.isEmpty()
        val isValid: Boolean = false,
        // val isPatientFirstNameValid: Boolean = true, // Becoming static
        // val isPatientLastNameValid: Boolean = true, // Becoming static
        val isBirthDateValid: Boolean = true, // Becoming static, but kept for consistency if PatientData model requires it
        
        // Unified compilation timestamp
        val compilationTimestamp: Long = System.currentTimeMillis(),
        val isCompilationTimestampValid: Boolean = true, // Default to true, can add validation later
        val compilationTimestampError: String? = null,

        val validationErrors: List<String> = emptyList(), // For the single snackbar message
        val invalidFieldKeys: Set<String> = emptySet(), // To identify specific invalid fields/cards

        // Caregiver selection state
        val selectedCaregiver: Contact? = null,
        val isCaregiverSelectionModalVisible: Boolean = false,
        val isAddingCaregiver: Boolean = false,

        // Temporary state for the AddCaregiverForm
        val newCaregiverFirstName: String = "",
        val newCaregiverLastName: String = "",
        val newCaregiverRelationship: String = "",
        val newCaregiverContact: String = "",
        val newCaregiverFirstNameError: String? = null,
        val newCaregiverLastNameError: String? = null,
        val newCaregiverRelationshipError: String? = null,
        val newCaregiverContactError: String? = null
    ) : CbiFormUiState()
    
    /**
     * Stato di salvataggio completato.
     *
     * @property form Form salvato
     */
    data class Saved(val formId: Int) : CbiFormUiState()
}

/**
 * Inizializza le sezioni del form CBI con le domande predefinite.
 *
 * @return Lista di sezioni inizializzate
 */
private fun initializeSections(): List<CBISection> {
    return listOf(
        CBISection(
            type = SectionType.OBJECTIVE,
            questions = CbiQuestions.getQuestionsForSection(SectionType.OBJECTIVE)
        ),
        CBISection(
            type = SectionType.PSYCHOLOGICAL,
            questions = CbiQuestions.getQuestionsForSection(SectionType.PSYCHOLOGICAL)
        ),
        CBISection(
            type = SectionType.PHYSICAL,
            questions = CbiQuestions.getQuestionsForSection(SectionType.PHYSICAL),
            correctionFactor = 1.25f
        ),
        CBISection(
            type = SectionType.SOCIAL,
            questions = CbiQuestions.getQuestionsForSection(SectionType.SOCIAL)
        ),
        CBISection(
            type = SectionType.EMOTIONAL,
            questions = CbiQuestions.getQuestionsForSection(SectionType.EMOTIONAL)
        )
    )
}
