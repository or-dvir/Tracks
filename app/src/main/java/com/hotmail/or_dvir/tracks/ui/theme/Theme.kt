package com.hotmail.or_dvir.tracks.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val secondaryColor = Color(0xffAB47BC)
private val secondaryVariantColor = Color(0xff8d24aa)
private val primaryVariantColor = Color(0xff303f9f)
private val statusBarDark = Color(0xff101010)

private val DarkColorPalette = darkColors(
    primary = Color(0xff9fa8da),
    primaryVariant = primaryVariantColor,
    secondary = secondaryColor,
    secondaryVariant = secondaryVariantColor
)

private val LightColorPalette = lightColors(
    primary = Color(0xff3f51b5),
    primaryVariant = primaryVariantColor,
    secondary = secondaryColor,
    secondaryVariant = secondaryVariantColor
)

@Composable
fun TracksTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = if (darkTheme) statusBarDark else primaryVariantColor
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}