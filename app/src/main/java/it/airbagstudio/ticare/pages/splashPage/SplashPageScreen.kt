package it.airbagstudio.ticare.pages.splashPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions

@Composable
fun SplashPageScreen(
    nav: NavigationActions,
    viewModel: SplashPageScreenViewModel = hiltViewModel()
    ) {
    Box{
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
        if(viewModel.loginSuccess){
            viewModel.loginSuccess = false
            nav.navigateToLogin()
        }

    }
}