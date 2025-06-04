package it.airbagstudio.ticare.pages.patientDetails.form.data.datasource.local

import android.content.Context
import it.airbagstudio.ticare.pages.patientDetails.form.data.datasource.JsonDataSource
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm // Added import
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm // Added import
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm // Added import
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.IOException

class JsonDataSourceImpl(
    private val context: Context,
    private val json: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true // Useful for potentially empty or slightly malformed files initially
    }
) : JsonDataSource {

    private val cbiFormsFilename = "cbi_forms.json"
    private val comidFormsFilename = "comid_forms.json"
    private val ipos3ggFormsFilename = "ipos_3gg_forms.json"
    private val ipos7ggFormsFilename = "ipos_7gg_forms.json" // Added filename
    private val seniorSittingAdesioneFormsFilename = "senior_sitting_adesione_forms.json" // Added filename
    private val seniorSittingNonAdesioneFormsFilename = "senior_sitting_non_adesione_forms.json" // Added filename

    override suspend fun saveCbiForms(forms: List<CBIForm>) {
        withContext(Dispatchers.IO) {
            try {
                val jsonString = json.encodeToString(forms)
                context.openFileOutput(cbiFormsFilename, Context.MODE_PRIVATE).use {
                    it.write(jsonString.toByteArray())
                }
            } catch (e: IOException) {
                // Log error or handle appropriately
                // For now, rethrow or wrap in a custom exception
                throw IOException("Error saving CBI forms to JSON: ${e.message}", e)
            }
        }
    }

    override suspend fun loadCbiForms(): List<CBIForm> {
        return withContext(Dispatchers.IO) {
            try {
                if (!context.getFileStreamPath(cbiFormsFilename).exists()) {
                    return@withContext emptyList<CBIForm>()
                }
                context.openFileInput(cbiFormsFilename).bufferedReader().use {
                    val jsonString = it.readText()
                    if (jsonString.isNotBlank()) {
                        json.decodeFromString<List<CBIForm>>(jsonString)
                    } else {
                        emptyList<CBIForm>()
                    }
                }
            } catch (e: FileNotFoundException) {
                // File doesn't exist, return empty list (normal case for first run)
                emptyList<CBIForm>()
            } catch (e: Exception) { // Catch broader exceptions for deserialization issues etc.
                // Log error or handle appropriately
                // For now, return empty list or rethrow
                // Consider logging e.g. Timber.e(e, "Error loading CBI forms from JSON")
                emptyList<CBIForm>() // Or throw custom exception
            }
        }
    }

    override suspend fun saveComidForms(forms: List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>) {
        withContext(Dispatchers.IO) {
            try {
                val jsonString = json.encodeToString(forms)
                context.openFileOutput(comidFormsFilename, Context.MODE_PRIVATE).use {
                    it.write(jsonString.toByteArray())
                }
            } catch (e: IOException) {
                throw IOException("Error saving COMID forms to JSON: ${e.message}", e)
            }
        }
    }

    override suspend fun loadComidForms(): List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm> {
        return withContext(Dispatchers.IO) {
            try {
                if (!context.getFileStreamPath(comidFormsFilename).exists()) {
                    return@withContext emptyList<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>()
                }
                context.openFileInput(comidFormsFilename).bufferedReader().use {
                    val jsonString = it.readText()
                    if (jsonString.isNotBlank()) {
                        json.decodeFromString<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>>(jsonString)
                    } else {
                        emptyList<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>()
                    }
                }
            } catch (e: FileNotFoundException) {
                emptyList<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>()
            } catch (e: Exception) {
                // Consider logging e.g. Timber.e(e, "Error loading COMID forms from JSON")
                emptyList<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>()
            }
        }
    }

    override suspend fun saveIPOS3ggForms(forms: List<IPOS3ggForm>) {
        withContext(Dispatchers.IO) {
            try {
                val jsonString = json.encodeToString(forms)
                context.openFileOutput(ipos3ggFormsFilename, Context.MODE_PRIVATE).use {
                    it.write(jsonString.toByteArray())
                }
            } catch (e: IOException) {
                throw IOException("Error saving IPOS3gg forms to JSON: ${e.message}", e)
            }
        }
    }

    override suspend fun loadIPOS3ggForms(): List<IPOS3ggForm> {
        return withContext(Dispatchers.IO) {
            try {
                if (!context.getFileStreamPath(ipos3ggFormsFilename).exists()) {
                    return@withContext emptyList<IPOS3ggForm>()
                }
                context.openFileInput(ipos3ggFormsFilename).bufferedReader().use {
                    val jsonString = it.readText()
                    if (jsonString.isNotBlank()) {
                        json.decodeFromString<List<IPOS3ggForm>>(jsonString)
                    } else {
                        emptyList<IPOS3ggForm>()
                    }
                }
            } catch (e: FileNotFoundException) {
                emptyList<IPOS3ggForm>()
            } catch (e: Exception) {
                // Consider logging e.g. Timber.e(e, "Error loading IPOS3gg forms from JSON")
                emptyList<IPOS3ggForm>()
            }
        }
    }

    override suspend fun saveIPOS7ggForms(forms: List<IPOS7ggForm>) {
        withContext(Dispatchers.IO) {
            try {
                val jsonString = json.encodeToString(forms)
                context.openFileOutput(ipos7ggFormsFilename, Context.MODE_PRIVATE).use {
                    it.write(jsonString.toByteArray())
                }
            } catch (e: IOException) {
                throw IOException("Error saving IPOS7gg forms to JSON: ${e.message}", e)
            }
        }
    }

    override suspend fun loadIPOS7ggForms(): List<IPOS7ggForm> {
        return withContext(Dispatchers.IO) {
            try {
                if (!context.getFileStreamPath(ipos7ggFormsFilename).exists()) {
                    return@withContext emptyList<IPOS7ggForm>()
                }
                context.openFileInput(ipos7ggFormsFilename).bufferedReader().use {
                    val jsonString = it.readText()
                    if (jsonString.isNotBlank()) {
                        json.decodeFromString<List<IPOS7ggForm>>(jsonString)
                    } else {
                        emptyList<IPOS7ggForm>()
                    }
                }
            } catch (e: FileNotFoundException) {
                emptyList<IPOS7ggForm>()
            } catch (e: Exception) {
                // Consider logging e.g. Timber.e(e, "Error loading IPOS7gg forms from JSON")
                emptyList<IPOS7ggForm>()
            }
        }
    }

    override suspend fun saveSeniorSittingAdesioneForms(forms: List<SeniorSittingAdesioneForm>) {
        withContext(Dispatchers.IO) {
            try {
                val jsonString = json.encodeToString(forms)
                context.openFileOutput(seniorSittingAdesioneFormsFilename, Context.MODE_PRIVATE).use {
                    it.write(jsonString.toByteArray())
                }
            } catch (e: IOException) {
                throw IOException("Error saving SeniorSittingAdesione forms to JSON: ${e.message}", e)
            }
        }
    }

    override suspend fun loadSeniorSittingAdesioneForms(): List<SeniorSittingAdesioneForm> {
        return withContext(Dispatchers.IO) {
            try {
                if (!context.getFileStreamPath(seniorSittingAdesioneFormsFilename).exists()) {
                    return@withContext emptyList<SeniorSittingAdesioneForm>()
                }
                context.openFileInput(seniorSittingAdesioneFormsFilename).bufferedReader().use {
                    val jsonString = it.readText()
                    if (jsonString.isNotBlank()) {
                        json.decodeFromString<List<SeniorSittingAdesioneForm>>(jsonString)
                    } else {
                        emptyList<SeniorSittingAdesioneForm>()
                    }
                }
            } catch (e: FileNotFoundException) {
                emptyList<SeniorSittingAdesioneForm>()
            } catch (e: Exception) {
                // Consider logging e.g. Timber.e(e, "Error loading SeniorSittingAdesione forms from JSON")
                emptyList<SeniorSittingAdesioneForm>()
            }
        }
    }

    override suspend fun saveSeniorSittingNonAdesioneForms(forms: List<SeniorSittingNonAdesioneForm>) {
        withContext(Dispatchers.IO) {
            try {
                val jsonString = json.encodeToString(forms)
                context.openFileOutput(seniorSittingNonAdesioneFormsFilename, Context.MODE_PRIVATE).use {
                    it.write(jsonString.toByteArray())
                }
            } catch (e: IOException) {
                throw IOException("Error saving SeniorSittingNonAdesione forms to JSON: ${e.message}", e)
            }
        }
    }

    override suspend fun loadSeniorSittingNonAdesioneForms(): List<SeniorSittingNonAdesioneForm> {
        return withContext(Dispatchers.IO) {
            try {
                if (!context.getFileStreamPath(seniorSittingNonAdesioneFormsFilename).exists()) {
                    return@withContext emptyList<SeniorSittingNonAdesioneForm>()
                }
                context.openFileInput(seniorSittingNonAdesioneFormsFilename).bufferedReader().use {
                    val jsonString = it.readText()
                    if (jsonString.isNotBlank()) {
                        json.decodeFromString<List<SeniorSittingNonAdesioneForm>>(jsonString)
                    } else {
                        emptyList<SeniorSittingNonAdesioneForm>()
                    }
                }
            } catch (e: FileNotFoundException) {
                emptyList<SeniorSittingNonAdesioneForm>()
            } catch (e: Exception) {
                // Consider logging e.g. Timber.e(e, "Error loading SeniorSittingNonAdesione forms from JSON")
                emptyList<SeniorSittingNonAdesioneForm>()
            }
        }
    }
}
