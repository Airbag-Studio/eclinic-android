package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.common.caregiverView

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.ticare.eclinic.library.entity.Contact
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.CaregiverSelectorButton

@Composable
fun CaregiverView(
    viewModel: CaregiverFromViewModel = hiltViewModel(),
    selectedCaregiver: Contact?,
    onSelectCaregiver: (Contact) -> Unit){
    var openCaregiverSelectionModal by remember { mutableStateOf(false) }
    var contactCreateUiState = viewModel.contactCreateUiState.collectAsState()

    Column {
        if (selectedCaregiver == null) {
            CaregiverSelectorButton(onClick = { openCaregiverSelectionModal = true })
        } else {
            CaregiverInfoCard(caregiver = selectedCaregiver)
            Spacer(modifier = Modifier.height(8.dp))
            CaregiverSelectorButton(onClick = { openCaregiverSelectionModal = true })
        }

        if (openCaregiverSelectionModal) {
            CaregiverSelectionModalSheet(
                viewModel = viewModel,
                onDismiss = { openCaregiverSelectionModal = false },
                onCaregiverSelected = { caregiver ->
                    openCaregiverSelectionModal = false
                    onSelectCaregiver(caregiver)
                                      },
                onCancelAddCaregiver = { openCaregiverSelectionModal = false },
            )
        }
    }

}