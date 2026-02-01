package org.bak.inflationmemorygame

import androidx.compose.runtime.Composable
import org.bak.inflationmemorygame.util.isRealWindowWidthCompact

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val useCustomFont: Boolean = true
}

actual fun getPlatform(): Platform = WasmPlatform()

@Composable
actual fun isWindowWidthCompact(): Boolean {
    return isRealWindowWidthCompact()
}

actual fun isPreloadNeeded(): Boolean = true