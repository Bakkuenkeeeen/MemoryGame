package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.OnTurnStartEffectHandler

class PlusOne: AbilityCard(actual = Abilities.PlusOne) {
    override fun onTurnStart(): OnTurnStartEffectHandler? {
        return null
    }
}