package it.airbagstudio.ticare.ui.components




data class PatientListItem(
    val address: String,
    val age: Int,
    val birthday: String,
    val cAP: String,
    val cOD: String,
    val locality: String,
    val name: String,
    val photo: String?,
    val surname: String
)