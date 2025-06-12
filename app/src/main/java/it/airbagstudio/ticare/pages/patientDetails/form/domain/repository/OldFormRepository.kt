package it.airbagstudio.ticare.pages.patientDetails.form.domain.repository

import ch.ticare.eclinic.library.entity.CaseDetail
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm
import kotlinx.coroutines.flow.Flow

/**
 * Interfaccia per il repository che gestisce le operazioni sui form.
 */
interface OldFormRepository {

    fun getUserDetails():CaseDetail?
    /**
     * Salva un form CBI. Se esiste già un form con lo stesso ID, viene aggiornato.
     * Altrimenti, viene aggiunto un nuovo form.
     *
     * @param form Il form CBI da salvare.
     */
    suspend fun saveCbiForm(form: CBIForm)

    /**
     * Recupera tutti i form CBI salvati.
     *
     * @return Un Flow che emette la lista dei form CBI.
     */
    fun getCbiForms(): Flow<List<CBIForm>> // Using Flow for reactive updates

    /**
     * Recupera un form CBI specifico tramite il suo ID.
     *
     * @param formId L'ID del form da recuperare.
     * @return Il form CBI se trovato, altrimenti null.
     */
    suspend fun getCbiFormById(formId: String): CBIForm?

    /**
     * Elimina un form CBI specifico tramite il suo ID.
     *
     * @param formId L'ID del form da eliminare.
     */
    suspend fun deleteCbiForm(formId: String)

    /**
     * Saves a COMID form. If a form with the same ID already exists, it is updated.
     * Otherwise, a new form is added.
     *
     * @param form The COMID form to save.
     */
    suspend fun saveComidForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm)

    /**
     * Retrieves all saved COMID forms.
     *
     * @return A Flow that emits the list of COMID forms.
     */
    fun getComidForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>>

    /**
     * Retrieves a specific COMID form by its ID.
     *
     * @param formId The ID of the form to retrieve.
     * @return The COMID form if found, otherwise null.
     */
    suspend fun getComidFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm?

    /**
     * Deletes a specific COMID form by its ID.
     *
     * @param formId The ID of the form to delete.
     */
    suspend fun deleteComidForm(formId: String)

    /**
     * Saves an IPOS3gg form. If a form with the same ID already exists, it is updated.
     * Otherwise, a new form is added.
     *
     * @param form The IPOS3gg form to save.
     */
    suspend fun saveIPOS3ggForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm)

    /**
     * Retrieves all saved IPOS3gg forms.
     *
     * @return A Flow that emits the list of IPOS3gg forms.
     */
    fun getIPOS3ggForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm>>

    /**
     * Retrieves a specific IPOS3gg form by its ID.
     *
     * @param formId The ID of the form to retrieve.
     * @return The IPOS3gg form if found, otherwise null.
     */
    suspend fun getIPOS3ggFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm?

    /**
     * Deletes a specific IPOS3gg form by its ID.
     *
     * @param formId The ID of the form to delete.
     */
    suspend fun deleteIPOS3ggForm(formId: String)

    /**
     * Saves an IPOS7gg form. If a form with the same ID already exists, it is updated.
     * Otherwise, a new form is added.
     *
     * @param form The IPOS7gg form to save.
     */
    suspend fun saveIPOS7ggForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm)

    /**
     * Retrieves all saved IPOS7gg forms.
     *
     * @return A Flow that emits the list of IPOS7gg forms.
     */
    fun getIPOS7ggForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm>>

    /**
     * Retrieves a specific IPOS7gg form by its ID.
     *
     * @param formId The ID of the form to retrieve.
     * @return The IPOS7gg form if found, otherwise null.
     */
    suspend fun getIPOS7ggFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm?

    /**
     * Deletes a specific IPOS7gg form by its ID.
     *
     * @param formId The ID of the form to delete.
     */
    suspend fun deleteIPOS7ggForm(formId: String)

    /**
     * Saves a SeniorSittingAdesione form. If a form with the same ID already exists, it is updated.
     * Otherwise, a new form is added.
     *
     * @param form The SeniorSittingAdesione form to save.
     */
    suspend fun saveSeniorSittingAdesioneForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm)

    /**
     * Retrieves all saved SeniorSittingAdesione forms.
     *
     * @return A Flow that emits the list of SeniorSittingAdesione forms.
     */
    fun getSeniorSittingAdesioneForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm>>

    /**
     * Retrieves a specific SeniorSittingAdesione form by its ID.
     *
     * @param formId The ID of the form to retrieve.
     * @return The SeniorSittingAdesione form if found, otherwise null.
     */
    suspend fun getSeniorSittingAdesioneFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm?

    /**
     * Deletes a specific SeniorSittingAdesione form by its ID.
     *
     * @param formId The ID of the form to delete.
     */
    suspend fun deleteSeniorSittingAdesioneForm(formId: String)

    /**
     * Saves a SeniorSittingNonAdesione form. If a form with the same ID already exists, it is updated.
     * Otherwise, a new form is added.
     *
     * @param form The SeniorSittingNonAdesione form to save.
     */
    suspend fun saveSeniorSittingNonAdesioneForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm)

    /**
     * Retrieves all saved SeniorSittingNonAdesione forms.
     *
     * @return A Flow that emits the list of SeniorSittingNonAdesione forms.
     */
    fun getSeniorSittingNonAdesioneForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm>>

    /**
     * Retrieves a specific SeniorSittingNonAdesione form by its ID.
     *
     * @param formId The ID of the form to retrieve.
     * @return The SeniorSittingNonAdesione form if found, otherwise null.
     */
    suspend fun getSeniorSittingNonAdesioneFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm?

    /**
     * Deletes a specific SeniorSittingNonAdesione form by its ID.
     *
     * @param formId The ID of the form to delete.
     */
    suspend fun deleteSeniorSittingNonAdesioneForm(formId: String)
}
