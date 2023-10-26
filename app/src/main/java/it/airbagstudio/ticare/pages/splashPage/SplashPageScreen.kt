package it.airbagstudio.ticare.pages.splashPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun SplashPageScreen(
    nav: NavigationActions,
    viewModel: SplashPageScreenViewModel = hiltViewModel()
    ) {
    if(viewModel.isLoggedIn == true){
        viewModel.isLoggedIn = null
        nav.navigateToPatientsList()
    } else if (viewModel.isLoggedIn == false){
        viewModel.isLoggedIn = null
        nav.navigateToLogin()
    }
    if (viewModel.errorMessage != null){
        ErrorAlert(message = viewModel.errorMessage!!, onDismissRequest = {
            viewModel.errorMessage = null
        })
    }
        BuildContent()


}

@Composable
private fun BuildContent(){
    Box {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.mipmap.bg_sdplash),
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Image(

                painter = painterResource(id = R.drawable.logo_eclinic),
                contentDescription = stringResource(
                    id = R.string.app_name
                )
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Image(

                painter = painterResource(id = R.drawable.ti_care_logo),
                modifier = Modifier.padding(bottom = 80.dp),
                contentDescription = stringResource(
                    id = R.string.app_name
                )
            )
        }
    }

}

@Composable
@Preview
private fun PreviewSplash(){
    AppTheme {
        BuildContent()
    }
}