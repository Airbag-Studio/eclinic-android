package it.airbagstudio.ticare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val offlineOnlineRepository: OfflineOnlineRepository
): ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun checkIfDataIsExpired(){
        viewModelScope.launch(coroutineExceptionHandler) {
            offlineOnlineRepository.checkIfDataIsExpired()
        }
    }
}