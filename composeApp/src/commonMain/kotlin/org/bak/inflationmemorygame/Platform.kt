package org.bak.inflationmemorygame

import androidx.compose.runtime.Composable

interface Platform {
    val name: String
    val useCustomFont: Boolean get() = false
}

expect fun getPlatform(): Platform

@Composable
expect fun isWindowWidthCompact(): Boolean

expect fun isPreloadNeeded(): Boolean