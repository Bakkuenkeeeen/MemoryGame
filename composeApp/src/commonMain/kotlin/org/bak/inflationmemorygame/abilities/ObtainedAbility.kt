package org.bak.inflationmemorygame.abilities

import kotlin.random.Random

abstract class ObtainedAbility : Ability {

    /** 同一の獲得済み能力を区別するためのID. */
    val instanceId: Long = Random.nextLong()

    abstract fun onTurnStart(): OnTurnStartEffectHandler?
}