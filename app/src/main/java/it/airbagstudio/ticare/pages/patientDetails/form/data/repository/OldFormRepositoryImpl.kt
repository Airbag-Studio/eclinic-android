package it.airbagstudio.ticare.pages.patientDetails.form.data.repository

import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.repository.UserDetailRepository
import it.airbagstudio.ticare.pages.patientDetails.form.data.datasource.JsonDataSource
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class OldFormRepositoryImpl(
    private val userDetailRepository: UserDetailRepository,
    private val jsonDataSource: JsonDataSource
) : OldFormRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _cbiForms = MutableStateFlow<List<CBIForm>>(emptyList())
    private val _comidForms = MutableStateFlow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>>(emptyList())
    private val _ipos3ggForms = MutableStateFlow<List<IPOS3ggForm>>(emptyList()) // Added StateFlow
    private val _ipos7ggForms = MutableStateFlow<List<IPOS7ggForm>>(emptyList()) // Added StateFlow for IPOS7gg
    private val _seniorSittingAdesioneForms = MutableStateFlow<List<SeniorSittingAdesioneForm>>(emptyList()) // Added StateFlow
    private val _seniorSittingNonAdesioneForms = MutableStateFlow<List<SeniorSittingNonAdesioneForm>>(emptyList()) // Added StateFlow

    init {
        repositoryScope.launch {
            _cbiForms.value = safeLoadCbiForms()
            _comidForms.value = safeLoadComidForms()
            _ipos3ggForms.value = safeLoadIPOS3ggForms() // Initialize IPOS3gg forms
            _ipos7ggForms.value = safeLoadIPOS7ggForms() // Initialize IPOS7gg forms
            _seniorSittingAdesioneForms.value = safeLoadSeniorSittingAdesioneForms() // Initialize SeniorSittingAdesione forms
            _seniorSittingNonAdesioneForms.value = safeLoadSeniorSittingNonAdesioneForms() // Initialize SeniorSittingNonAdesione forms
        }
    }

    override fun getUserDetails(): CaseDetail? {
        return userDetailRepository.getCurrentCase()
    }

    private suspend fun safeLoadCbiForms(): List<CBIForm> {
        return try {
            jsonDataSource.loadCbiForms()
        } catch (e: Exception) {
            // Log error, decide if to propagate or return empty
            // Timber.e(e, "Error loading CBI forms in repository")
            emptyList()
        }
    }

    private suspend fun safeLoadComidForms(): List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm> {
        return try {
            jsonDataSource.loadComidForms()
        } catch (e: Exception) {
            // Log error, decide if to propagate or return empty
            // Timber.e(e, "Error loading COMID forms in repository")
            emptyList()
        }
    }

    private suspend fun safeLoadIPOS3ggForms(): List<IPOS3ggForm> { // Added safe load method
        return try {
            jsonDataSource.loadIPOS3ggForms()
        } catch (e: Exception) {
            // Log error, decide if to propagate or return empty
            // Timber.e(e, "Error loading IPOS3gg forms in repository")
            emptyList()
        }
    }

    private suspend fun safeLoadIPOS7ggForms(): List<IPOS7ggForm> { // Added safe load method for IPOS7gg
        return try {
            jsonDataSource.loadIPOS7ggForms()
        } catch (e: Exception) {
            // Log error, decide if to propagate or return empty
            // Timber.e(e, "Error loading IPOS7gg forms in repository")
            emptyList()
        }
    }

    private suspend fun safeLoadSeniorSittingAdesioneForms(): List<SeniorSittingAdesioneForm> {
        return try {
            jsonDataSource.loadSeniorSittingAdesioneForms()
        } catch (e: Exception) {
            // Log error
            emptyList()
        }
    }

    private suspend fun safeLoadSeniorSittingNonAdesioneForms(): List<SeniorSittingNonAdesioneForm> {
        return try {
            jsonDataSource.loadSeniorSittingNonAdesioneForms()
        } catch (e: Exception) {
            // Log error
            emptyList()
        }
    }

    override suspend fun saveCbiForm(form: CBIForm) {
        try {
            // Perform IO on IO dispatcher
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadCbiForms().toMutableList()
                val existingIndex = currentForms.indexOfFirst { it.id == form.id }

                if (existingIndex != -1) {
                    // Update existing form
                    currentForms[existingIndex] = form
                } else {
                    // Add new form
                    currentForms.add(form)
                }
                jsonDataSource.saveCbiForms(currentForms)
                currentForms.toList() // Return the updated list
            }
            _cbiForms.value = updatedList // Update the StateFlow
        } catch (e: IOException) {
            // Handle or rethrow specific repository-level exception
            // Timber.e(e, "Error saving CBI form in repository")
            throw e // Or wrap in a custom domain/repository exception
        }
    }

    override fun getCbiForms(): Flow<List<CBIForm>> = _cbiForms.asStateFlow()

    override suspend fun getCbiFormById(formId: String): CBIForm? {
        // Can still load directly or use the flow's current value if appropriate
        // For simplicity and directness, loading from source:
        return try {
            jsonDataSource.loadCbiForms().find { it.id == formId }
        } catch (e: IOException) {
            // Timber.e(e, "Error getting CBI form by ID in repository")
            null // Or throw custom domain/repository exception
        }
    }

    override suspend fun deleteCbiForm(formId: String) {
        try {
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadCbiForms().toMutableList()
                val removed = currentForms.removeIf { it.id == formId }
                if (removed) {
                    jsonDataSource.saveCbiForms(currentForms)
                }
                currentForms.toList() // Return the potentially modified list
            }
            // Always update the flow, even if the item wasn't found and list is same,
            // or if it was removed.
            _cbiForms.value = updatedList
        } catch (e: IOException) {
            // Timber.e(e, "Error deleting CBI form in repository")
            // Handle or rethrow specific repository-level exception
            throw e // Or wrap in a custom domain/repository exception
        }
    }

    // COMID Form Methods
    override suspend fun saveComidForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm) {
        try {
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadComidForms().toMutableList()
                val existingIndex = currentForms.indexOfFirst { it.id == form.id }

                if (existingIndex != -1) {
                    currentForms[existingIndex] = form
                } else {
                    currentForms.add(form)
                }
                jsonDataSource.saveComidForms(currentForms)
                currentForms.toList()
            }
            _comidForms.value = updatedList
        } catch (e: IOException) {
            // Timber.e(e, "Error saving COMID form in repository")
            throw e
        }
    }

    override fun getComidForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>> = _comidForms.asStateFlow()

    override suspend fun getComidFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm? {
        return try {
            jsonDataSource.loadComidForms().find { it.id == formId }
        } catch (e: IOException) {
            // Timber.e(e, "Error getting COMID form by ID in repository")
            null
        }
    }

    override suspend fun deleteComidForm(formId: String) {
        try {
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadComidForms().toMutableList()
                val removed = currentForms.removeIf { it.id == formId }
                if (removed) {
                    jsonDataSource.saveComidForms(currentForms)
                }
                currentForms.toList()
            }
            _comidForms.value = updatedList
        } catch (e: IOException) {
            // Timber.e(e, "Error deleting COMID form in repository")
            throw e
        }
    }

    // IPOS3gg Form Methods
    override suspend fun saveIPOS3ggForm(form: IPOS3ggForm) {
        try {
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadIPOS3ggForms().toMutableList()
                val existingIndex = currentForms.indexOfFirst { it.id == form.id }

                if (existingIndex != -1) {
                    currentForms[existingIndex] = form
                } else {
                    currentForms.add(form)
                }
                jsonDataSource.saveIPOS3ggForms(currentForms)
                currentForms.toList()
            }
            _ipos3ggForms.value = updatedList
        } catch (e: IOException) {
            // Timber.e(e, "Error saving IPOS3gg form in repository")
            throw e
        }
    }

    override fun getIPOS3ggForms(): Flow<List<IPOS3ggForm>> = _ipos3ggForms.asStateFlow()

    override suspend fun getIPOS3ggFormById(formId: String): IPOS3ggForm? {
        return try {
            // Prioritizing consistency by loading fresh from datasource,
            // though _ipos3ggForms.value.find { it.id == formId } could be faster if staleness is acceptable.
            jsonDataSource.loadIPOS3ggForms().find { it.id == formId }
        } catch (e: IOException) {
            // Timber.e(e, "Error getting IPOS3gg form by ID in repository")
            null
        }
    }

    override suspend fun deleteIPOS3ggForm(formId: String) {
        try {
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadIPOS3ggForms().toMutableList()
                val removed = currentForms.removeIf { it.id == formId }
                if (removed) {
                    jsonDataSource.saveIPOS3ggForms(currentForms)
                }
                currentForms.toList()
            }
            _ipos3ggForms.value = updatedList
        } catch (e: IOException) {
            // Timber.e(e, "Error deleting IPOS3gg form in repository")
            throw e
        }
    }

    // IPOS7gg Form Methods
    override suspend fun saveIPOS7ggForm(form: IPOS7ggForm) {
        try {
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadIPOS7ggForms().toMutableList()
                val existingIndex = currentForms.indexOfFirst { it.id == form.id }

                if (existingIndex != -1) {
                    currentForms[existingIndex] = form
                } else {
                    currentForms.add(form)
                }
                jsonDataSource.saveIPOS7ggForms(currentForms)
                currentForms.toList()
            }
            _ipos7ggForms.value = updatedList
        } catch (e: IOException) {
            // Timber.e(e, "Error saving IPOS7gg form in repository")
            throw e
        }
    }

    override fun getIPOS7ggForms(): Flow<List<IPOS7ggForm>> = _ipos7ggForms.asStateFlow()

    override suspend fun getIPOS7ggFormById(formId: String): IPOS7ggForm? {
        return try {
            jsonDataSource.loadIPOS7ggForms().find { it.id == formId }
        } catch (e: IOException) {
            // Timber.e(e, "Error getting IPOS7gg form by ID in repository")
            null
        }
    }

    override suspend fun deleteIPOS7ggForm(formId: String) {
        try {
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadIPOS7ggForms().toMutableList()
                val removed = currentForms.removeIf { it.id == formId }
                if (removed) {
                    jsonDataSource.saveIPOS7ggForms(currentForms)
                }
                currentForms.toList()
            }
            _ipos7ggForms.value = updatedList
        } catch (e: IOException) {
            // Timber.e(e, "Error deleting IPOS7gg form in repository")
            throw e
        }
    }

    // SeniorSittingAdesione Form Methods
    override suspend fun saveSeniorSittingAdesioneForm(form: SeniorSittingAdesioneForm) {
        try {
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadSeniorSittingAdesioneForms().toMutableList()
                val existingIndex = currentForms.indexOfFirst { it.id == form.id }

                if (existingIndex != -1) {
                    currentForms[existingIndex] = form
                } else {
                    currentForms.add(form)
                }
                jsonDataSource.saveSeniorSittingAdesioneForms(currentForms)
                currentForms.toList()
            }
            _seniorSittingAdesioneForms.value = updatedList
        } catch (e: IOException) {
            // Timber.e(e, "Error saving SeniorSittingAdesione form in repository")
            throw e
        }
    }

    override fun getSeniorSittingAdesioneForms(): Flow<List<SeniorSittingAdesioneForm>> = _seniorSittingAdesioneForms.asStateFlow()

    override suspend fun getSeniorSittingAdesioneFormById(formId: String): SeniorSittingAdesioneForm? {
        return try {
            jsonDataSource.loadSeniorSittingAdesioneForms().find { it.id == formId }
        } catch (e: IOException) {
            // Timber.e(e, "Error getting SeniorSittingAdesione form by ID in repository")
            null
        }
    }

    override suspend fun deleteSeniorSittingAdesioneForm(formId: String) {
        try {
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadSeniorSittingAdesioneForms().toMutableList()
                val removed = currentForms.removeIf { it.id == formId }
                if (removed) {
                    jsonDataSource.saveSeniorSittingAdesioneForms(currentForms)
                }
                currentForms.toList()
            }
            _seniorSittingAdesioneForms.value = updatedList
        } catch (e: IOException) {
            // Timber.e(e, "Error deleting SeniorSittingAdesione form in repository")
            throw e
        }
    }

    // SeniorSittingNonAdesione Form Methods
    override suspend fun saveSeniorSittingNonAdesioneForm(form: SeniorSittingNonAdesioneForm) {
        try {
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadSeniorSittingNonAdesioneForms().toMutableList()
                val existingIndex = currentForms.indexOfFirst { it.id == form.id }

                if (existingIndex != -1) {
                    currentForms[existingIndex] = form
                } else {
                    currentForms.add(form)
                }
                jsonDataSource.saveSeniorSittingNonAdesioneForms(currentForms)
                currentForms.toList()
            }
            _seniorSittingNonAdesioneForms.value = updatedList
        } catch (e: IOException) {
            // Timber.e(e, "Error saving SeniorSittingNonAdesione form in repository")
            throw e
        }
    }

    override fun getSeniorSittingNonAdesioneForms(): Flow<List<SeniorSittingNonAdesioneForm>> = _seniorSittingNonAdesioneForms.asStateFlow()

    override suspend fun getSeniorSittingNonAdesioneFormById(formId: String): SeniorSittingNonAdesioneForm? {
        return try {
            jsonDataSource.loadSeniorSittingNonAdesioneForms().find { it.id == formId }
        } catch (e: IOException) {
            // Timber.e(e, "Error getting SeniorSittingNonAdesione form by ID in repository")
            null
        }
    }

    override suspend fun deleteSeniorSittingNonAdesioneForm(formId: String) {
        try {
            val updatedList = withContext(Dispatchers.IO) {
                val currentForms = jsonDataSource.loadSeniorSittingNonAdesioneForms().toMutableList()
                val removed = currentForms.removeIf { it.id == formId }
                if (removed) {
                    jsonDataSource.saveSeniorSittingNonAdesioneForms(currentForms)
                }
                currentForms.toList()
            }
            _seniorSittingNonAdesioneForms.value = updatedList
        } catch (e: IOException) {
            // Timber.e(e, "Error deleting SeniorSittingNonAdesione form in repository")
            throw e
        }
    }
}
