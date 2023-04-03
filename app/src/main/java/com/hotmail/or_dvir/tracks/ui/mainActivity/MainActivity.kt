package com.hotmail.or_dvir.tracks.ui.mainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.hotmail.or_dvir.tracks.ui.collectIsDarkMode
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreen
import com.hotmail.or_dvir.tracks.ui.theme.TracksTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // todo there is a delay until isDarkMode is loaded, and the screen
            //  is "light theme" until then. i need a splash screen!!!!
            TracksTheme(viewModel.collectIsDarkMode()) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigator(HomeScreen()) {
                        // todo test all transitions on REAL device
                        FadeTransition(it)
//                        SlideTransition(it)
//                        ScaleTransition(it)
                    }
                }
            }
        }
    }
}
