package org.bak.inflationmemorygame

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import org.bak.inflationmemorygame.util.isRealWindowWidthCompact

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

@Composable
actual fun isWindowWidthCompact(): Boolean {
    return isRealWindowWidthCompact()
}