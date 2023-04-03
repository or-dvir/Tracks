package com.hotmail.or_dvir.tracks.ui.mainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.hotmail.or_dvir.tracks.collectAsStateLifecycleAware
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreen
import com.hotmail.or_dvir.tracks.ui.theme.TracksTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkMode =
                viewModel.isDarkModeFlow.collectAsStateLifecycleAware(initial = false).value

            successfully listening to datastore changes. next implement menu in screens to change this value
            TracksTheme(isDarkMode) {
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
