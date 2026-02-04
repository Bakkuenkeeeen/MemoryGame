package org.bak.inflationmemorygame

actual fun loadSettings(): Settings {
    // TODO SharedPreference
    return VolatileSettings()
}