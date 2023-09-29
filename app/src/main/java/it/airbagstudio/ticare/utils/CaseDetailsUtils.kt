package it.airbagstudio.ticare.utils

import ch.ticare.eclinic.library.entity.CaseDetail

fun CaseDetail.getCompleteName(): String{
    return "$surname $name"
}