package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.comid

import ch.ticare.eclinic.library.entity.form.ComidTest

fun ComidTest.getTotalScore(): Int{
    return (this.tableETotalScore ?: 0) + (this.tableATotalScore  ?: 0) + (this.tableBTotalScore  ?: 0) + (this.tableCTotalScore  ?: 0) + (this.tableDTotalScore ?: 0) + (this.tableETotalScore ?: 0)
}