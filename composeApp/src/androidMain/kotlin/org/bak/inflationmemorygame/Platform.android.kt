package org.bak.inflationmemorygame

import android.os.Build
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowSizeClass

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun isWindowWidthCompact(): Boolean {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val isCompact by remember {
        derivedStateOf {
            !windowAdaptiveInfo.windowSizeClass
                .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
        }
    }
    return isCompact
}

actual fun isPreloadNeeded(): Boolean = false