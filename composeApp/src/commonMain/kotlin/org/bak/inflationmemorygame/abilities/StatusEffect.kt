package org.bak.inflationmemorygame.abilities

data class StatusEffect(
    val parentAbilityInstanceId: Long,
    val amount: Int,
    val calculationType: CalculationType
) {
    sealed class CalculationType {
        data object Add : CalculationType()
        data class Fixed(val priority: Int): CalculationType()
    }
}
