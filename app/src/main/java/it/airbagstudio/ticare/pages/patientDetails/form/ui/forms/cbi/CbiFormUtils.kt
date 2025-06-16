package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi

import ch.ticare.eclinic.library.entity.form.GetCBITestList.CBITestResult


fun CBITestResult.getTotalScore(): Int{
    return tableDTotalScore + tableETotalScore + tableFTotalScore + tableSTotalScore + tableTTotalScore
}