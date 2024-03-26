package it.airbagstudio.ticare.pages.coursesGeneric.data

import ch.ticare.eclinic.library.entity.AddHomeCareCourse
import ch.ticare.eclinic.library.entity.EditHomeCareCourse
import ch.ticare.eclinic.library.entity.GenericSaveResponse
import ch.ticare.eclinic.library.entity.HomeCareCourse
import ch.ticare.eclinic.library.entity.HomeCareCourseCategory
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.entity.WrapperResponse
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import ch.ticare.eclinic.library.repository.NursingCourseRepository
import it.airbagstudio.ticare.R
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
        ToolTag.BloodExamTask -> TODO()
        ToolTag.PhysiotherapyTask -> TODO()
        ToolTag.NursingTask -> TODO()
        ToolTag.EducatorTask -> TODO()
        ToolTag.ErgotherapyTask -> TODO()
        ToolTag.AtelierTask -> TODO()
        ToolTag.ActivatorTask -> TODO()
        ToolTag.GenericTask -> TODO()
        ToolTag.HomeCareCourse -> R.string.care_planes
        ToolTag.PhysiotherapyCourse -> R.string.physiotherapy_course
        ToolTag.ErgotherapyCourse -> R.string.ergotherapy_course
        ToolTag.AtelierCourse -> R.string.atelier_course
        ToolTag.EducatorCourse -> TODO()
        ToolTag.ActivatorCourse -> R.string.activator_course
        ToolTag.NursingCourse -> R.string.nursing_courses
        ToolTag.Diary -> TODO()
        ToolTag.CarePlan -> TODO()
        ToolTag.Wounds -> TODO()
        ToolTag.OtherServices -> TODO()
    }
}

fun ToolTag.getCreateLabelId(): Int{
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
        ToolTag.PhysiotherapyCourse ->  R.string.new_physiotherapy_course
        ToolTag.ErgotherapyCourse ->  R.string.new_ergotherapy_course
        ToolTag.AtelierCourse -> R.string.new_atelier_course
        ToolTag.EducatorCourse -> TODO()
        ToolTag.ActivatorCourse ->  R.string.new_activator_course
        ToolTag.NursingCourse -> R.string.new_nursing_course
        ToolTag.Diary -> TODO()
        ToolTag.CarePlan -> TODO()
        ToolTag.Wounds -> TODO()
        ToolTag.OtherServices -> TODO()
    }
}