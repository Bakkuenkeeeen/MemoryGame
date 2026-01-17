package org.bak.inflationmemorygame.abilities

import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.plus_one
import org.jetbrains.compose.resources.DrawableResource

enum class Abilities(
    override val displayName: String,
    override val description: String,
    override val image: DrawableResource
): Ability {
    PlusOne(
        "プラスワン",
        "めくれる回数が1回増える。",
        Res.drawable.plus_one
    )
}