package org.bak.inflationmemorygame.abilities

import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.plus_one
import org.bak.inflationmemorygame.params.MaxOccurrence
import org.jetbrains.compose.resources.DrawableResource

enum class Abilities(
    override val displayName: String,
    override val description: String,
    override val image: DrawableResource,
    override val maxOccurrenceInStage: Int,
    override val maxOccurrenceInGame: Int
): Ability {
    PlusOne(
        displayName = "プラスワン",
        description = "めくれる回数が1回増える。",
        image = Res.drawable.plus_one,
        maxOccurrenceInStage = MaxOccurrence.PLUS_ONE_IN_STAGE,
        maxOccurrenceInGame = MaxOccurrence.PLUS_ONE_IN_GAME
    )
}