package it.airbagstudio.ticare.pages.patientDetails.form.ui.navigation

// import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi.CbiFormViewModel // ViewModel is instantiated directly
// import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.comid.ComidFormViewModel // ViewModel is instantiated directly

/**
 * Definizione delle route di navigazione dell'app.
 */
object AppDestinations {
    private const val HOME_ROUTE_BASE = "home"
    const val HOME_ROUTE_SAVED_ARG = "saved" // Made public for HomeScreen to use
    const val HOME_ROUTE = "$HOME_ROUTE_BASE?$HOME_ROUTE_SAVED_ARG={$HOME_ROUTE_SAVED_ARG}"

    const val CBI_FORM_ROUTE_BASE = "cbi_form"
    const val CBI_FORM_ID_ARG = "formId"
    const val CBI_FORM_ROUTE = "$CBI_FORM_ROUTE_BASE/{$CBI_FORM_ID_ARG}"

    const val COMID_FORM_ROUTE_BASE = "comid_form"
    const val COMID_FORM_ID_ARG = "formId" // Can reuse "formId" as arg name
    const val COMID_FORM_ROUTE = "$COMID_FORM_ROUTE_BASE/{$COMID_FORM_ID_ARG}"

    const val IPOS_FORM_ROUTE_BASE = "ipos_form"
    const val IPOS_FORM_ID_ARG = "formId"
    const val IPOS_FORM_ROUTE = "$IPOS_FORM_ROUTE_BASE/{$IPOS_FORM_ID_ARG}"

    const val SENIOR_SITTING_FORM_ROUTE_BASE = "senior_sitting_form"
    const val SENIOR_SITTING_FORM_ID_ARG = "formId"
    const val SENIOR_SITTING_FORM_ROUTE = "$SENIOR_SITTING_FORM_ROUTE_BASE/{$SENIOR_SITTING_FORM_ID_ARG}"

    const val IPOS3GG_FORM_ROUTE_BASE = "ipos3gg_form"
    const val IPOS3GG_FORM_ID_ARG = "formId" // Can reuse "formId"
    const val IPOS3GG_FORM_ROUTE = "$IPOS3GG_FORM_ROUTE_BASE/{$IPOS3GG_FORM_ID_ARG}"

    const val IPOS7GG_FORM_ROUTE_BASE = "ipos7gg_form" // Added for IPOS7gg
    const val IPOS7GG_FORM_ID_ARG = "formId" // Can reuse "formId"
    const val IPOS7GG_FORM_ROUTE = "$IPOS7GG_FORM_ROUTE_BASE/{$IPOS7GG_FORM_ID_ARG}" // Added for IPOS7gg

    const val SENIOR_SITTING_ADESIONE_FORM_ROUTE_BASE = "senior_sitting_adesione_form"
    const val SENIOR_SITTING_ADESIONE_FORM_ID_ARG = "formId"
    const val SENIOR_SITTING_ADESIONE_FORM_ROUTE = "$SENIOR_SITTING_ADESIONE_FORM_ROUTE_BASE/{$SENIOR_SITTING_ADESIONE_FORM_ID_ARG}"

    const val SENIOR_SITTING_NON_ADESIONE_FORM_ROUTE_BASE = "senior_sitting_non_adesione_form"
    const val SENIOR_SITTING_NON_ADESIONE_FORM_ID_ARG = "formId"
    const val SENIOR_SITTING_NON_ADESIONE_FORM_ROUTE = "$SENIOR_SITTING_NON_ADESIONE_FORM_ROUTE_BASE/{$SENIOR_SITTING_NON_ADESIONE_FORM_ID_ARG}"

    const val IDPALL_FORM_ROUTE_BASE = "idpall_form"
    const val IDPALL_FORM_ID_ARG = "formId"
    const val IDPALL_FORM_ROUTE = "$IDPALL_FORM_ROUTE_BASE/{$IDPALL_FORM_ID_ARG}"

    const val CAM_FORM_ROUTE_BASE = "cam_form" // Added for CAM
    const val CAM_FORM_ID_ARG = "formId" // Added for CAM
    const val CAM_FORM_ROUTE = "$CAM_FORM_ROUTE_BASE/{$CAM_FORM_ID_ARG}" // Added for CAM

    /**
     * Crea la route per la home screen, opzionalmente indicando se un form è stato salvato.
     * @param saved true se un form è stato appena salvato, false altrimenti.
     * @return Route completa per la home.
     */
    fun homeRoute(saved: Boolean = false): String {
        return "$HOME_ROUTE_BASE?$HOME_ROUTE_SAVED_ARG=$saved"
    }

    /**
     * Crea la route per il form CBI con un ID opzionale.
     *
     * @param formId ID del form (null per nuovo form)
     * @return Route completa
     */
    fun cbiFormRoute(formId: String? = null): String {
        return "$CBI_FORM_ROUTE_BASE/${formId ?: "new"}"
    }

    fun comidFormRoute(formId: String? = null): String {
        return "$COMID_FORM_ROUTE_BASE/${formId ?: "new"}"
    }

    fun iposFormRoute(formId: String? = null): String {
        return "$IPOS_FORM_ROUTE_BASE/${formId ?: "new"}"
    }

    fun seniorSittingFormRoute(formId: String? = null): String {
        return "$SENIOR_SITTING_FORM_ROUTE_BASE/${formId ?: "new"}"
    }

    fun idpallFormRoute(formId: String? = null): String {
        return "${IDPALL_FORM_ROUTE_BASE}/${formId ?: "new"}"
    }

    fun camFormRoute(formId: String? = null): String { // Added for CAM
        return "$CAM_FORM_ROUTE_BASE/${formId ?: "new"}"
    }
}
