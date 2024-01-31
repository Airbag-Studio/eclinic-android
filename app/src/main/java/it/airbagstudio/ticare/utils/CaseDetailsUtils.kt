package it.airbagstudio.ticare.utils

import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.CaseInfo

fun CaseDetail.getCompleteName(): String{
    return "$surname $name"
}

fun CaseInfo.getCompleteName(): String{
    return "$surname $name"
}