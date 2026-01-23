package org.bak.inflationmemorygame.abilities

import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.plus_one
import inflationmemorygame.composeapp.generated.resources.superhuman
import org.bak.inflationmemorygame.values.AbilityText
import org.bak.inflationmemorygame.values.AbilityTexts
import org.bak.inflationmemorygame.values.MaxOccurrence
import org.bak.inflationmemorygame.values.MaxOccurrences
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.max

enum class Abilities(
    text: AbilityText,
    maxOccurrence: MaxOccurrence,
    override val image: DrawableResource
) : Ability {
    PlusOne(
        text = AbilityTexts.PlusOne,
        maxOccurrence = MaxOccurrences.PlusOne,
        image = Res.drawable.plus_one
    ),
    Superhuman(
        text = AbilityTexts.Superhuman,
        maxOccurrence = MaxOccurrences.Superhuman,
        image = Res.drawable.superhuman
    )
    ;

    override val displayName: String = text.displayName
    override val description: String = text.description
    override val maxOccurrenceInStage: Int = maxOccurrence.inStage
    override val maxOccurrenceInGame: Int = maxOccurrence.inGame
}
