package org.bak.inflationmemorygame

actual fun loadSettings(): Settings {
    // TODO デスクトップアプリ用の設定値保存の仕組み
    return VolatileSettings()
}