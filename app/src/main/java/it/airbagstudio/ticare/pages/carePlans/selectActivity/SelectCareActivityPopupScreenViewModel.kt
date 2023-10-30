package it.airbagstudio.ticare.pages.carePlans.selectActivity

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectCareActivityPopupScreenViewModel @Inject constructor(): ViewModel() {

    val query = mutableStateOf("")
}