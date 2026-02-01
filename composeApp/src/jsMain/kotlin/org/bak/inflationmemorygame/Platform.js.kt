package org.bak.inflationmemorygame

import androidx.compose.runtime.Composable
import org.bak.inflationmemorygame.util.isRealWindowWidthCompact

class JsPlatform : Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()

@Composable
actual fun isWindowWidthCompact(): Boolean {
    return isRealWindowWidthCompact()
}

actual fun isPreloadNeeded(): Boolean = true