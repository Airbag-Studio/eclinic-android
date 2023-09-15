package it.airbagstudio.ticare.navigation

import androidx.navigation.NavController
import it.airbagstudio.ticare.navigation.Screens.LOGIN_SCREEN
import it.airbagstudio.ticare.navigation.Screens.SPLASH_SCREEN

private object Screens{
    const val SPLASH_SCREEN = "splashScreen"
    const val LOGIN_SCREEN = "loginScreen"
}
object Destinations{
    const val SPLASH_ROUTE = SPLASH_SCREEN
    const val LOGIN_ROUTE = LOGIN_SCREEN
}

class NavigationActions(private val navController: NavController){

    fun navigateToSplash(){
        navController.navigate(SPLASH_SCREEN)
    }

    fun navigateToLogin(){
        navController.navigate(LOGIN_SCREEN)
    }
}