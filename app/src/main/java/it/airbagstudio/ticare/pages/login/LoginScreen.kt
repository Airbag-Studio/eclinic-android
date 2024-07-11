package it.airbagstudio.ticare.pages.login

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.RestrictionsManager
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.components.AppVersionText
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ListPopup
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.seed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigationActions: NavigationActions

) {
    val localContext = LocalContext.current


    val showStructuresDialog = remember { mutableStateOf(false) }
    val showFirstLoginDialog = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DisposableEffect(localContext) {
        val intentFilter = IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED)
        viewModel.resolveRestrictions(localContext)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                viewModel.resolveRestrictions(localContext)
            }
        }

        localContext.registerReceiver(receiver, intentFilter)

        onDispose {
            localContext.unregisterReceiver(receiver)
        }

    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Image(
                    painter = painterResource(id = R.drawable.logo_eclinic),
                    contentDescription = stringResource(
                        id = R.string.app_name
                    ),
                    modifier = Modifier.width(114.dp)
                )
            }
        )
    }) { values ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(values)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            if (!uiState.pageState.showSecondStep) {
                OutlinedTextField(
                    singleLine = true,
                    value = uiState.loginData.server ?: "",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.downloadCompanies()
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    label = {
                        Text(text = stringResource(id = R.string.server))
                    }, onValueChange = { newValue ->
                        viewModel.setServer(newValue)
                    })

                OutlinedTextField(

                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    enabled = false,
                    trailingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.id_dropdown),
                            contentDescription = stringResource(
                                id = R.string.structure
                            )
                        )
                    },
                    value = uiState.loginData.company ?: "",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp)
                        .alpha(if (uiState.pageState.companies.isEmpty()) 0.2f else 1f)
                        .clickable {
                            if (uiState.pageState.companies.isNotEmpty()) {
                                showStructuresDialog.value = true
                            }
                        },
                    label = {
                        Text(text = stringResource(id = R.string.structure))
                    }, onValueChange = { _ ->

                    })


            } else {

                CompanyAndStructureView(
                    address = uiState.loginData.server ?: "",
                    companyName = uiState.loginData.company ?: ""
                ) {
                    viewModel.setSecondStep(false)
                }

                OutlinedTextField(
                    enabled = uiState.loginData.company?.isNotEmpty() == true,
                    value = uiState.loginData.username,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    label = {
                        Text(text = stringResource(id = R.string.user))
                    }, onValueChange = { newValue ->
                        viewModel.setUserName(newValue)
                    })

                OutlinedTextField(
                    enabled = uiState.loginData.company?.isNotEmpty() == true,
                    value = uiState.loginData.password,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    label = {
                        Text(text = stringResource(id = R.string.password))
                    }, onValueChange = { newValue ->
                        viewModel.setPassword(newValue)
                    })

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp)

                ) {
                    Text(
                        text = stringResource(id = R.string.remember_me),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Checkbox(
                        colors = CheckboxDefaults.colors(
                            checkedColor = seed
                        ),
                        checked = uiState.loginData.rememberMe,
                        onCheckedChange = {
                            viewModel.setRememberMe(it)
                        })

                }
            }


            Spacer(modifier = Modifier.weight(1f))
            val isButtonEnabled = if (uiState.pageState.showSecondStep) {
                uiState.loginData.isValid && !uiState.pageState.isLoading
            } else {
                !uiState.loginData.server.isNullOrEmpty() && !uiState.loginData.company.isNullOrEmpty() && !uiState.pageState.isLoading
            }
            Button(
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = seed
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 64.dp),
                onClick = {
                    if (uiState.pageState.showSecondStep) {
                        viewModel.loginUser()
                    } else {
                        viewModel.setSecondStep(true)
                    }

                }) {
                if (uiState.pageState.showSecondStep) {
                    Text(text = stringResource(id = R.string.login))
                } else {
                    Text(text = stringResource(id = R.string.enter))
                }

                if (uiState.pageState.isLoading) {
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            AppVersionText(modifier = Modifier.fillMaxWidth())
        }
        if (showStructuresDialog.value) {
            val popupItems =
                uiState.pageState.companies.map { ListPopupItem(label = it.name, item = it) }
            ListPopup(
                title = stringResource(id = R.string.structure),
                items = popupItems,
                setShowDialog = {
                    showStructuresDialog.value = false
                },
                onItemSelected = {
                    it.item?.let { it1 -> viewModel.setCompany(it1) }
                    showStructuresDialog.value = false
                })
        }
        if (showFirstLoginDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    showFirstLoginDialog.value = false
                },
                title = { Text(text = stringResource(id = R.string.first_login)) },
                text = { Text(text = stringResource(id = R.string.first_login_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showFirstLoginDialog.value = false
                        }) {
                        Text("Ok")
                    }
                })
        }
        if (uiState.pageState.successLogin) {
            viewModel.reset()
            navigationActions.navigateToPatientsList()
        }
        if (uiState.pageState.errorMessage != null) {
            ErrorAlert(
                message = uiState.pageState.errorMessage!!,
                onDismissRequest = { viewModel.reset() },
                onRetry = {
                    viewModel.downloadCompanies()
                })
        }
    }
}

@Composable
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
private fun LoginScreenPreview() {
    AppTheme {
        LoginScreen(navigationActions = NavigationActions(navController = NavController(LocalContext.current)))
    }
}