package it.airbagstudio.ticare.utils

import ch.ticare.eclinic.library.entity.Gender
import it.airbagstudio.ticare.R

fun Gender.getIconId(): Int{
    return when(this.code){
        "M" -> R.drawable.ic_male
        "F" -> R.drawable.ic_female
        else -> R.drawable.ic_agender
    }
}