package it.airbagstudio.ticare.pages.patientDetails.form.data.datasource

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm

/**
 * Interfaccia per il DataSource che gestisce le operazioni di I/O su file JSON.
 */
interface JsonDataSource {

    /**
     * Salva una lista di form CBI nel file JSON.
     * Sovrascrive completamente il contenuto del file.
     *
     * @param forms Lista dei form CBI da salvare.
     */
    suspend fun saveCbiForms(forms: List<CBIForm>)

    /**
     * Carica la lista dei form CBI dal file JSON.
     *
     * @return Lista dei form CBI. Restituisce una lista vuota se il file non esiste o è vuoto.
     */
    suspend fun loadCbiForms(): List<CBIForm>

    /**
     * Saves a list of COMID forms to the JSON file.
     * Completely overwrites the file content.
     *
     * @param forms List of COMID forms to save.
     */
    suspend fun saveComidForms(forms: List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>)

    /**
     * Loads the list of COMID forms from the JSON file.
     *
     * @return List of COMID forms. Returns an empty list if the file does not exist or is empty.
     */
    suspend fun loadComidForms(): List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>

    /**
     * Saves a list of IPOS3gg forms to the JSON file.
     * Completely overwrites the file content.
     *
     * @param forms List of IPOS3gg forms to save.
     */
    suspend fun saveIPOS3ggForms(forms: List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm>)

    /**
     * Loads the list of IPOS3gg forms from the JSON file.
     *
     * @return List of IPOS3gg forms. Returns an empty list if the file does not exist or is empty.
     */
    suspend fun loadIPOS3ggForms(): List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm>

    /**
     * Saves a list of IPOS7gg forms to the JSON file.
     * Completely overwrites the file content.
     *
     * @param forms List of IPOS7gg forms to save.
     */
    suspend fun saveIPOS7ggForms(forms: List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm>)

    /**
     * Loads the list of IPOS7gg forms from the JSON file.
     *
     * @return List of IPOS7gg forms. Returns an empty list if the file does not exist or is empty.
     */
    suspend fun loadIPOS7ggForms(): List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm>

    /**
     * Saves a list of SeniorSittingAdesione forms to the JSON file.
     * Completely overwrites the file content.
     *
     * @param forms List of SeniorSittingAdesione forms to save.
     */
    suspend fun saveSeniorSittingAdesioneForms(forms: List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm>)

    /**
     * Loads the list of SeniorSittingAdesione forms from the JSON file.
     *
     * @return List of SeniorSittingAdesione forms. Returns an empty list if the file does not exist or is empty.
     */
    suspend fun loadSeniorSittingAdesioneForms(): List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm>

    /**
     * Saves a list of SeniorSittingNonAdesione forms to the JSON file.
     * Completely overwrites the file content.
     *
     * @param forms List of SeniorSittingNonAdesione forms to save.
     */
    suspend fun saveSeniorSittingNonAdesioneForms(forms: List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm>)

    /**
     * Loads the list of SeniorSittingNonAdesione forms from the JSON file.
     *
     * @return List of SeniorSittingNonAdesione forms. Returns an empty list if the file does not exist or is empty.
     */
    suspend fun loadSeniorSittingNonAdesioneForms(): List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm>

    // Though not directly used by UI, good to have for completeness if backend/repo needs it.
//    suspend fun deleteSeniorSittingNonAdesioneForm(formId: String)
}
