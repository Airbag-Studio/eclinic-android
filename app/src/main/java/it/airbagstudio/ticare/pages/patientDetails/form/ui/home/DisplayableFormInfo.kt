package it.airbagstudio.ticare.pages.patientDetails.form.ui.home

/**
 * A common data class to represent any form in the home screen list.
 * This allows for a heterogeneous list of different form types.
 *
 * @property id The unique ID of the form.
 * @property formType A string identifier for the type of form (e.g., "CBI", "COMID").
 * @property displayName A user-friendly name or title for the form instance (e.g., Patient Name + Date).
 * @property creationDate Timestamp of when the form was created.
 */
data class DisplayableFormInfo(
    val id: String,
    val formType: String,
    val displayName: String, // Could be constructed from patient name + date, or specific form title
    val creationDate: Long,
    val totalPoints: Int? = null,
)
