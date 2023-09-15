package it.airbagstudio.ticare.navigation

import androidx.navigation.NavController
import it.airbagstudio.ticare.navigation.Screens.SPLASH_SCREEN

private object Screens{
    const val SPLASH_SCREEN = "splashScreen"
}
object Destinations{
    const val SPLASH_ROUTE = SPLASH_SCREEN
}

class NavigationActions(private val navController: NavController){

    fun navigateToSplash(){
        navController.navigate(SPLASH_SCREEN)
    }
}