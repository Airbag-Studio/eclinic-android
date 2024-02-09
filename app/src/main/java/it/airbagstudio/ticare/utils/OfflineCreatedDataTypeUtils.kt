package it.airbagstudio.ticare.utils

import androidx.compose.ui.res.stringResource
import ch.ticare.eclinic.library.entity.OfflineCreatedDataType
import it.airbagstudio.ticare.R


fun OfflineCreatedDataType.getCategoryName(): Int {
    return when(this){
        OfflineCreatedDataType.WOUND_CREATE -> R.string.wounds
        OfflineCreatedDataType.WOUND_ADD_IMAGE -> R.string.image
        OfflineCreatedDataType.WOUND_ADD_CHECK -> R.string.check
        OfflineCreatedDataType.WOUND_CLOSE -> R.string.close_wound
        OfflineCreatedDataType.AGENDA_UPDATE_TASK -> R.string.drug_administration
        OfflineCreatedDataType.AGENDA_ADD_UNSCHEDULED -> R.string.vital_parameters
        OfflineCreatedDataType.AGENDA_UPDATE_UNSCHEDULED -> R.string.care_planes
        OfflineCreatedDataType.OTHER_SERVICE_CREATE -> R.string.other_prescriptions
        OfflineCreatedDataType.OTHER_SERVICE_UPDATE -> R.string.other_prescriptions
        OfflineCreatedDataType.NURSING_CREATE -> R.string.new_nursing_course
        OfflineCreatedDataType.NURSING_UPDATE -> R.string.new_nursing_course
        OfflineCreatedDataType.PLANS_CREATE ->  R.string.care_planes
        OfflineCreatedDataType.PLANS_UPDATE ->  R.string.care_planes
        OfflineCreatedDataType.USER_MARKING_ADD -> R.string.working_hours_title
    }
}