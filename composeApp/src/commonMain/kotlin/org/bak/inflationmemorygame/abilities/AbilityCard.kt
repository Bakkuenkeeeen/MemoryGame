package org.bak.inflationmemorygame.abilities

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
abstract class AbilityCard(private val actual: Abilities) : Ability by actual {

    /** 表になっているかどうか. */
    var isFaceUp: Boolean by mutableStateOf(false)

    /** 「虚無」化しているかどうか. */
    var isVoidOut: Boolean by mutableStateOf(false)

    /** クリック可能かどうか. */
    var isInteractionEnabled: Boolean by mutableStateOf(true)

    abstract fun onTurnStart(): OnTurnStartEffectHandler?
}