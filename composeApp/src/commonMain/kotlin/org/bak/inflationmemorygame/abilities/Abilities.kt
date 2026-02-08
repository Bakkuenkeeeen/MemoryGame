package org.bak.inflationmemorygame.abilities

import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.hirameki
import inflationmemorygame.composeapp.generated.resources.hold
import inflationmemorygame.composeapp.generated.resources.oikaze
import inflationmemorygame.composeapp.generated.resources.plus_one
import inflationmemorygame.composeapp.generated.resources.superhuman
import org.bak.inflationmemorygame.values.AbilityText
import org.bak.inflationmemorygame.values.AbilityTexts
import org.bak.inflationmemorygame.values.MaxOccurrence
import org.bak.inflationmemorygame.values.MaxOccurrences
import org.jetbrains.compose.resources.DrawableResource

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
    ),
    Oikaze(
        text = AbilityTexts.Oikaze,
        maxOccurrence = MaxOccurrences.Oikaze,
        image = Res.drawable.oikaze
    ),
    Hirameki(
        text = AbilityTexts.Hirameki,
        maxOccurrence = MaxOccurrences.Hirameki,
        image = Res.drawable.hirameki
    ),
    Hold(
        text = AbilityTexts.Hold,
        maxOccurrence = MaxOccurrences.Hold,
        image = Res.drawable.hold
    )
    ;

    override val displayName: String = text.displayName
    override val description: String = text.description
    override val maxOccurrenceInStage: Int = maxOccurrence.inStage
    override val maxOccurrenceInGame: Int = maxOccurrence.inGame
}
