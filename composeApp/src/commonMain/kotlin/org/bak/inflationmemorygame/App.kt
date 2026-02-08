package org.bak.inflationmemorygame

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import inflationmemorygame.composeapp.generated.resources.NotoSansJP_Black
import inflationmemorygame.composeapp.generated.resources.NotoSansJP_Bold
import inflationmemorygame.composeapp.generated.resources.NotoSansJP_ExtraBold
import inflationmemorygame.composeapp.generated.resources.NotoSansJP_ExtraLight
import inflationmemorygame.composeapp.generated.resources.NotoSansJP_Light
import inflationmemorygame.composeapp.generated.resources.NotoSansJP_Medium
import inflationmemorygame.composeapp.generated.resources.NotoSansJP_Regular
import inflationmemorygame.composeapp.generated.resources.NotoSansJP_SemiBold
import inflationmemorygame.composeapp.generated.resources.NotoSansJP_Thin
import inflationmemorygame.composeapp.generated.resources.Res
import org.bak.inflationmemorygame.components.LoadingScreen
import org.bak.inflationmemorygame.dialogs.Dialogs
import org.bak.inflationmemorygame.game.GameScreen
import org.bak.inflationmemorygame.game.gameStateViewModel
import org.jetbrains.compose.resources.Font

@Composable
@Preview
fun App() {
    MaterialTheme(typography = fontFamily()?.let { fontFamily ->
        Typography().run {
            copy(
                displayLarge = displayLarge.copy(fontFamily = fontFamily),
                displayMedium = displayMedium.copy(fontFamily = fontFamily),
                displaySmall = displaySmall.copy(fontFamily = fontFamily),
                headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
                headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
                headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
                titleLarge = titleLarge.copy(fontFamily = fontFamily),
                titleMedium = titleMedium.copy(fontFamily = fontFamily),
                titleSmall = titleSmall.copy(fontFamily = fontFamily),
                bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
                bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
                bodySmall = bodySmall.copy(fontFamily = fontFamily),
                labelLarge = labelLarge.copy(fontFamily = fontFamily),
                labelMedium = labelMedium.copy(fontFamily = fontFamily),
                labelSmall = labelSmall.copy(fontFamily = fontFamily)
            )
        }
    } ?: MaterialTheme.typography) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val state = gameStateViewModel()
            GameScreen(modifier = Modifier.padding(innerPadding), state = state)
            AnimatedVisibility(visible = state.isPreloading, exit = fadeOut()) {
                LoadingScreen(modifier = Modifier.padding(innerPadding))
            }
            // ダイアログは全画面表示のためinnerPaddingは使わない
            Dialogs(state = state)
        }
    }
}

@Composable
fun fontFamily() = if (getPlatform().useCustomFont) {
    FontFamily(
        Font(Res.font.NotoSansJP_Bold, FontWeight.Bold),
        Font(Res.font.NotoSansJP_Thin, FontWeight.Thin),
        Font(Res.font.NotoSansJP_Black, FontWeight.Black),
        Font(Res.font.NotoSansJP_Light, FontWeight.Light),
        Font(Res.font.NotoSansJP_Medium, FontWeight.Medium),
        Font(Res.font.NotoSansJP_ExtraBold, FontWeight.ExtraBold),
        Font(Res.font.NotoSansJP_ExtraLight, FontWeight.ExtraLight),
        Font(Res.font.NotoSansJP_Regular, FontWeight.Normal),
        Font(Res.font.NotoSansJP_SemiBold, FontWeight.SemiBold),
    )
} else {
    null
}