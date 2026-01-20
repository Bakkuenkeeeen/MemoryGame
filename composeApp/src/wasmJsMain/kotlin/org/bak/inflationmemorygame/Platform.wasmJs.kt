package org.bak.inflationmemorygame

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val useCustomFont: Boolean = true
}

actual fun getPlatform(): Platform = WasmPlatform()
fun foo() {
}