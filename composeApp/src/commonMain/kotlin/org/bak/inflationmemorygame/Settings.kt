package org.bak.inflationmemorygame

interface Settings {
    val shouldShowDetailWhenDiscover: Boolean
    val isConfirmAutoFlipSkip: Boolean

    fun update(
        shouldShowDetailWhenDiscover: Boolean? = null,
        isConfirmAutoFlipSkip: Boolean? = null
    )
}

class VolatileSettings(
    override var shouldShowDetailWhenDiscover: Boolean = true,
    override var isConfirmAutoFlipSkip: Boolean = false
) : Settings {
    override fun update(
        shouldShowDetailWhenDiscover: Boolean?,
        isConfirmAutoFlipSkip: Boolean?
    ) {
        shouldShowDetailWhenDiscover?.let {
            this.shouldShowDetailWhenDiscover = it
        }
        isConfirmAutoFlipSkip?.let {
            this.isConfirmAutoFlipSkip = it
        }
    }
}

expect fun loadSettings(): Settings