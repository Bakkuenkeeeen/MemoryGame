package org.bak.inflationmemorygame

interface Platform {
    val name: String
    val useCustomFont: Boolean get() = false
}

expect fun getPlatform(): Platform