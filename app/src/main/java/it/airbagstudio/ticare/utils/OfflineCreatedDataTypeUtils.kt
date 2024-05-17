package it.airbagstudio.ticare.utils

import androidx.compose.ui.res.stringResource
import ch.ticare.eclinic.library.entity.OfflineCreatedDataType
import it.airbagstudio.ticare.R


fun OfflineCreatedDataType.getCategoryName(): Int {
    return when (this) {
        OfflineCreatedDataType.WOUND_CREATE, OfflineCreatedDataType.WOUND_UPDATE -> R.string.wounds
        OfflineCreatedDataType.WOUND_ADD_IMAGE -> R.string.image
        OfflineCreatedDataType.WOUND_ADD_CHECK, OfflineCreatedDataType.WOUND_UPDATE_CHECK -> R.string.check
        OfflineCreatedDataType.WOUND_CLOSE -> R.string.close_wound
        OfflineCreatedDataType.AGENDA_UPDATE_TASK -> R.string.drug_administration
        OfflineCreatedDataType.AGENDA_ADD_UNSCHEDULED -> R.string.vital_parameters
        OfflineCreatedDataType.AGENDA_UPDATE_UNSCHEDULED -> R.string.care_planes
        OfflineCreatedDataType.OTHER_SERVICE_CREATE -> R.string.other_prescriptions
        OfflineCreatedDataType.OTHER_SERVICE_UPDATE -> R.string.other_prescriptions
        OfflineCreatedDataType.NURSING_CREATE -> R.string.new_nursing_course
        OfflineCreatedDataType.NURSING_UPDATE -> R.string.new_nursing_course
        OfflineCreatedDataType.PLANS_CREATE -> R.string.care_planes
        OfflineCreatedDataType.PLANS_UPDATE -> R.string.care_planes
        OfflineCreatedDataType.USER_MARKING_ADD -> R.string.working_hours_title
        OfflineCreatedDataType.NURSING_ADD_IMAGE -> R.string.image
        OfflineCreatedDataType.HOME_CARE_COURSE_CREATE, OfflineCreatedDataType.HOME_CARE_COURSE_UPDATE -> R.string.new_nursing_course
        OfflineCreatedDataType.PHYSIOTHERAPY_COURSE_CREATE, OfflineCreatedDataType.PHYSIOTHERAPY_COURSE_UPDATE -> R.string.physiotherapy_course
        OfflineCreatedDataType.ERGOTHERAPY_COURSE_CREATE, OfflineCreatedDataType.ERGOTHERAPY_COURSE_UPDATE -> R.string.ergotherapy_course
        OfflineCreatedDataType.ATELIER_COURSE_CREATE, OfflineCreatedDataType.ATELIER_COURSE_UPDATE -> R.string.atelier_course
        OfflineCreatedDataType.EDUCATOR_COURSE_CREATE, OfflineCreatedDataType.EDUCATOR_COURSE_UPDATE -> R.string.educator_course
        OfflineCreatedDataType.ACTIVATOR_COURSE_CREATE, OfflineCreatedDataType.ACTIVATOR_COURSE_UPDATE -> R.string.activator_course
        OfflineCreatedDataType.ACTIVATOR_CREATE, OfflineCreatedDataType.ACTIVATOR_UPDATE -> R.string.activator_task
        OfflineCreatedDataType.ATELIER_CREATE, OfflineCreatedDataType.ATELIER_UPDATE -> R.string.atelier_task
        OfflineCreatedDataType.BLOODEXAM_CREATE, OfflineCreatedDataType.BLOODEXAM_UPDATE -> R.string.blood_exam_task
        OfflineCreatedDataType.EDUCATOR_CREATE, OfflineCreatedDataType.EDUCATOR_UPDATE -> R.string.educator_task
        OfflineCreatedDataType.ERGOTHERAPY_CREATE, OfflineCreatedDataType.ERGOTHERAPY_UPDATE -> R.string.ergotherapy_task
        OfflineCreatedDataType.GENERICSERVICE_CREATE, OfflineCreatedDataType.GENERICSERVICE_UPDATE -> R.string.generic_task
        OfflineCreatedDataType.PHYSIOTHERAPY_CREATE, OfflineCreatedDataType.PHYSIOTHERAPY_UPDATE -> R.string.physiotherapy_task
    }
}