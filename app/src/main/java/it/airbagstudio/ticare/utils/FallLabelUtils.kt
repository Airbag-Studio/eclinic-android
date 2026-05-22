package it.airbagstudio.ticare.utils

import androidx.annotation.StringRes
import ch.ticare.eclinic.library.entity.ClinicType
import it.airbagstudio.ticare.R

enum class FallField {
    Location,
    Cause,
    CauseDetail,
    Consequences,
    ConsequencesDetails,
    PreStatus,
    AutonomyDegree,
    WithRestraint,
    NonSlipShoes,
    FallWitnessesDetails
}

@StringRes
fun FallField.getLabelRes(clinicType: ClinicType?): Int {
    return when (clinicType) {
        ClinicType.SPITEX -> when (this) {
            FallField.Location       -> R.string.fall_location_spitex
            FallField.Cause          -> R.string.fall_cause_spitex
            FallField.CauseDetail    -> R.string.fall_cause
            FallField.Consequences   -> R.string.fall_cause_detail_spitex
            FallField.ConsequencesDetails -> R.string.fall_consequence_details_spitex
            FallField.PreStatus      -> R.string.fall_pre_status_spitex
            FallField.AutonomyDegree -> R.string.fall_autonomy_degree_spitex
            FallField.WithRestraint  -> R.string.fall_with_restraint_spitex
            FallField.NonSlipShoes   -> R.string.fall_non_slip_shoes_spitex
            FallField.FallWitnessesDetails -> R.string.fall_witnesses_details_spitex
        }
        else -> when (this) {
            FallField.Location       -> R.string.fall_location
            FallField.Cause          -> R.string.fall_cause
            FallField.CauseDetail    -> R.string.fall_cause_detail
            FallField.Consequences   -> R.string.fall_consequences
            FallField.ConsequencesDetails -> R.string.fall_consequence_details

            FallField.PreStatus      -> R.string.fall_pre_status
            FallField.AutonomyDegree -> R.string.fall_autonomy_degree
            FallField.WithRestraint  -> R.string.fall_with_restraint
            FallField.NonSlipShoes   -> R.string.fall_non_slip_shoes
            FallField.FallWitnessesDetails -> R.string.fall_witnesses_details
        }
    }
}
