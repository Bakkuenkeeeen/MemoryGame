package org.bak.inflationmemorygame.abilities

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandler

@Stable
abstract class GrowAbility(ability: Abilities) : EarnedAbility(ability = ability) {
    var level: Int by mutableStateOf(1)
        private set

    fun levelUp() {
        level++
    }

    override fun onLost(): OnAbilityLostEffectHandler? {
        return if (level == 1) {
            super.onLost()
        } else {
            level--
            onLevelDown()
        }
    }

    abstract fun onLevelDown(): OnAbilityLostEffectHandler?
}