package it.airbagstudio.ticare.pages.patientDetails.form.ui.home

// Import DisplayableFormInfo instead of CBIForm directly for the Success state
// import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm // No longer needed here

/**
 * Rappresenta i possibili stati della UI della home page.
 */
sealed class HomeUiState {
    /**
     * Stato di caricamento.
     */
    object Loading : HomeUiState()
    
    /**
     * Stato vuoto, nessun form salvato.
     */
    object Empty : HomeUiState()
    
    /**
     * Stato di successo con la mappa dei form raggruppati per tipo.
     * La chiave della mappa è il tipo di form (es. "CBI"),
     * e il valore è la lista dei form di quel tipo, rappresentati come [DisplayableFormInfo].
     */
    data class Success(val groupedForms: Map<String, List<DisplayableFormInfo>> = emptyMap()) : HomeUiState()

    /**
     * Stato di errore durante il caricamento o altre operazioni.
     */
    data class Error(val message: String) : HomeUiState()
}
