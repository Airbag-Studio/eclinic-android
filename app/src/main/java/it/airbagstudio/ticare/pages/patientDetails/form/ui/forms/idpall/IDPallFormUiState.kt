package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.idpall

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IDPallForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData

/**
 * Rappresenta i diversi stati dell'interfaccia utente per il form ID PALL.
 * Gestisce gli stati di caricamento, modifica, salvataggio ed errore.
 */
sealed class IDPallFormUiState {
    
    /**
     * Stato di caricamento iniziale del form.
     */
    object Loading : IDPallFormUiState()
    
    /**
     * Stato di errore con messaggio descrittivo.
     *
     * @param message Messaggio di errore da mostrare all'utente.
     */
    data class Error(val message: String) : IDPallFormUiState()
    
    /**
     * Stato di modifica del form, quando l'utente sta compilando o modificando i dati.
     *
     * @param form Il form ID PALL corrente con tutti i dati.
     * @param patientData I dati del paziente (separati per facilità di gestione).
     * @param isBirthDateValid Indica se la data di nascita inserita è valida.
     * @param isSaving Indica se è in corso un'operazione di salvataggio.
     */
    data class Editing(
        val form: IDPallForm,
        val patientData: PatientData,
        val isBirthDateValid: Boolean = true,
        val isSaving: Boolean = false,
        val isFormValid: Boolean = true
    ) : IDPallFormUiState()
    
    /**
     * Stato di form salvato con successo.
     *
     * @param savedForm Il form ID PALL che è stato salvato.
     */
    data class Saved(val savedForm: IDPallForm) : IDPallFormUiState()
}
