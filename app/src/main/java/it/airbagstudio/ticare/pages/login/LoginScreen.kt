package it.airbagstudio.ticare.pages.login

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.navigation.NavController
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ListPopup
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.seed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigationActions: NavigationActions

) {
    val showStructuresDialog = remember { mutableStateOf(false) }
    val showFirstLoginDialog = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
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

        ) {
            OutlinedTextField(
                value = viewModel.server,
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
                    viewModel.server = newValue
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
                value = viewModel.selectedCompany?.name ?: "",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 32.dp)
                    .alpha(if(viewModel.companies.isEmpty()) 0.2f else 1f)
                    .clickable {
                        if(viewModel.companies.isNotEmpty()) {
                            showStructuresDialog.value = true
                        }
                    },
                label = {
                    Text(text = stringResource(id = R.string.structure))
                }, onValueChange = { newValue ->

                })


            OutlinedTextField(
                enabled = viewModel.selectedCompany != null,
                value = viewModel.username,
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
                    viewModel.username = newValue
                })

            OutlinedTextField(
                enabled = viewModel.selectedCompany != null,
                value = viewModel.password,
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
                    viewModel.password = newValue
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
                    checked = viewModel.rememberMe,
                    onCheckedChange = {
                        viewModel.rememberMe = it
                    })

            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                enabled = viewModel.isValid.invoke() && !viewModel.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = seed
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 64.dp),
                onClick = {
                    if (!viewModel.isLoading) {
                        viewModel.loginUser()
                    }
                }) {
                Text(text = stringResource(id = R.string.login))
                if (viewModel.isLoading){
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

        }
        if (showStructuresDialog.value) {
            val popupItems = viewModel.companies.map { ListPopupItem(label = it.name, item = it) }
            ListPopup(title = stringResource(id = R.string.structure), items = popupItems, setShowDialog = {
                showStructuresDialog.value = false
            }, onItemSelected = {
                viewModel.selectedCompany = it.item
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
        if (viewModel.successLogin){
            viewModel.successLogin = false
            navigationActions.navigateToPatientsList()
        }
        if (viewModel.errorMessage != null){
            ErrorAlert(message = viewModel.errorMessage!!, onDismissRequest = { viewModel.errorMessage = null }, onRetry = {
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