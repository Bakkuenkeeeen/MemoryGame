package org.bak.inflationmemorygame

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform