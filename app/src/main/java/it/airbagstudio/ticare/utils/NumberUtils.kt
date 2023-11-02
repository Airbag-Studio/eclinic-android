package it.airbagstudio.ticare.utils

fun String.isValidVitalParameterValue() : Boolean{
    val splitted = this.split("/")
    if (splitted.count() == 2){
        return splitted[0].toDoubleOrNull() != null && splitted[1].toDoubleOrNull() != null
    }
    return this.toDoubleOrNull() != null
}