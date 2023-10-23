package it.airbagstudio.ticare.utils

import ch.ticare.eclinic.library.entity.OtherService

fun OtherService.getNumber(): String {
    return this.item.substringAfter("[").substringBefore("]")
}
fun OtherService.getItemDesc(): String{
    return this.item.substringBefore("[")
}