package org.bak.inflationmemorygame

actual fun loadSettings(): Settings {
    return VolatileSettings()
}