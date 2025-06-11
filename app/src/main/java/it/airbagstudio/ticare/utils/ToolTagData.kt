package it.airbagstudio.ticare.utils

import android.net.Uri
import ch.ticare.eclinic.library.entity.AddHomeCareCourse
import ch.ticare.eclinic.library.entity.EditHomeCareCourse
import ch.ticare.eclinic.library.entity.GenericSaveResponse
import ch.ticare.eclinic.library.entity.HomeCareCourse
import ch.ticare.eclinic.library.entity.HomeCareCourseCategory
import ch.ticare.eclinic.library.entity.OfflineSection
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.entity.WrapperResponse
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.SectionListItem
import it.airbagstudio.ticare.utils.format
import java.time.Duration
import java.util.Date

fun ToolTag.getType(): String{
    return this.name + "Type"
    /*
    return when(this){
        ToolTag.PharmacologicalTask -> TODO()
        ToolTag.VitalSignTask -> TODO()
        ToolTag.BloodExamTask -> TODO()
        ToolTag.PhysiotherapyTask -> TODO()
        ToolTag.NursingTask -> TODO()
        ToolTag.EducatorTask -> TODO()
        ToolTag.ErgotherapyTask -> TODO()
        ToolTag.AtelierTask -> TODO()
        ToolTag.ActivatorTask -> TODO()
        ToolTag.GenericTask -> TODO()
        ToolTag.HomeCareCourse -> TODO()
        ToolTag.PhysiotherapyCourse -> "PhysiotherapyCourseType"
        ToolTag.ErgotherapyCourse -> TODO()
        ToolTag.AtelierCourse -> TODO()
        ToolTag.EducatorCourse -> TODO()
        ToolTag.ActivatorCourse -> TODO()
        ToolTag.NursingCourse -> TODO()
        ToolTag.Diary -> TODO()
        ToolTag.CarePlan -> TODO()
        ToolTag.Wounds -> TODO()
        ToolTag.OtherServices -> TODO()
    }

     */
}

fun ToolTag.getLabelId(): Int{
    return when(this){
        ToolTag.PharmacologicalTask -> TODO()
        ToolTag.VitalSignTask -> TODO()
        ToolTag.BloodExamTask -> R.string.blood_exam_task
        ToolTag.PhysiotherapyTask -> R.string.physiotherapy_task
        ToolTag.NursingTask -> R.string.nursing_task
        ToolTag.EducatorTask -> R.string.educator_task
        ToolTag.ErgotherapyTask -> R.string.ergotherapy_task
        ToolTag.AtelierTask -> R.string.atelier_task
        ToolTag.ActivatorTask -> R.string.activator_task
        ToolTag.GenericServiceTask -> R.string.generic_task
        ToolTag.HomeCareCourse -> R.string.care_planes
        ToolTag.PhysiotherapyCourse -> R.string.physiotherapy_course
        ToolTag.ErgotherapyCourse -> R.string.ergotherapy_course
        ToolTag.AtelierCourse -> R.string.atelier_course
        ToolTag.EducatorCourse -> R.string.educator_course
        ToolTag.ActivatorCourse -> R.string.activator_course
        ToolTag.NursingCourse -> R.string.nursing_courses
        ToolTag.Diary -> TODO()
        ToolTag.HomeCareServiceCarePlan -> TODO()
        ToolTag.Wound -> TODO()
        ToolTag.OtherService -> TODO()
        ToolTag.MedicalCourse -> R.string.medical_course
        ToolTag.Scale -> R.string.scale
    }
}

fun ToolTag.getCreateLabelId(): Int{
    return when(this){
        ToolTag.PharmacologicalTask -> TODO()
        ToolTag.VitalSignTask -> TODO()
        ToolTag.BloodExamTask -> R.string.new_blood_exam_task
        ToolTag.PhysiotherapyTask -> R.string.new_physiotherapy_task
        ToolTag.NursingTask -> R.string.new_nursing_task
        ToolTag.EducatorTask -> R.string.new_educator_task
        ToolTag.ErgotherapyTask -> R.string.new_ergotherapy_task
        ToolTag.AtelierTask -> R.string.new_atelier_task
        ToolTag.ActivatorTask -> R.string.new_activator_task
        ToolTag.GenericServiceTask -> R.string.new_generic_task
        ToolTag.HomeCareCourse -> TODO()
        ToolTag.PhysiotherapyCourse ->  R.string.new_physiotherapy_course
        ToolTag.ErgotherapyCourse ->  R.string.new_ergotherapy_course
        ToolTag.AtelierCourse -> R.string.new_atelier_course
        ToolTag.EducatorCourse -> R.string.new_educator_course
        ToolTag.ActivatorCourse ->  R.string.new_activator_course
        ToolTag.NursingCourse -> R.string.new_nursing_course
        ToolTag.Diary -> TODO()
        ToolTag.HomeCareServiceCarePlan -> TODO()
        ToolTag.Wound -> TODO()
        ToolTag.OtherService -> TODO()
        ToolTag.MedicalCourse -> R.string.new_medical_course
        ToolTag.Scale -> R.string.new_scale
    }
}

fun ToolTag.getDiaryIconId(): Int{
    return when(this){
        ToolTag.PharmacologicalTask -> R.drawable.ic_pills
        ToolTag.VitalSignTask -> R.drawable.ic_vital_parameters
        ToolTag.BloodExamTask -> R.drawable.ic_blood_exam_task
        ToolTag.PhysiotherapyTask ->  R.drawable.ic_physiotherapy_task
        ToolTag.NursingTask ->  R.drawable.ic_nursing_task
        ToolTag.EducatorTask ->  R.drawable.ic_educator_task
        ToolTag.ErgotherapyTask -> R.drawable.ic_ergotherapy_task
        ToolTag.AtelierTask ->  R.drawable.ic_atelier_task
        ToolTag.ActivatorTask -> R.drawable.ic_activator_task
        ToolTag.GenericServiceTask ->  R.drawable.ic_generic_task
        ToolTag.HomeCareCourse -> R.drawable.ic_home_care_course
        ToolTag.PhysiotherapyCourse ->   R.drawable.ic_physiotherapy_course
        ToolTag.ErgotherapyCourse ->   R.drawable.ic_ergotherapy_course
        ToolTag.AtelierCourse ->  R.drawable.ic_atelier_course
        ToolTag.EducatorCourse ->  R.drawable.ic_educator_course
        ToolTag.ActivatorCourse ->   R.drawable.ic_activator_course
        ToolTag.NursingCourse ->  R.drawable.ic_nursing_courses
        ToolTag.Diary -> TODO()
        ToolTag.HomeCareServiceCarePlan -> R.drawable.ic_care_planes
        ToolTag.Wound -> R.drawable.ic_wounds
        ToolTag.OtherService -> R.drawable.ic_other_prescriptions
        ToolTag.MedicalCourse -> R.drawable.ic_medical_course
        ToolTag.Scale -> R.drawable.moduli
    }
}

fun ToolTag.canCreateNew(): Boolean{
    return when(this){
        ToolTag.PhysiotherapyTask,ToolTag.ErgotherapyTask -> false
        else -> true
    }
}
