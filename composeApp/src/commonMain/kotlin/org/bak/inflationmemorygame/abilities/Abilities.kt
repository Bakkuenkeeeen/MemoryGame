package org.bak.inflationmemorygame.abilities

import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.description_hirameki
import inflationmemorygame.composeapp.generated.resources.description_hold
import inflationmemorygame.composeapp.generated.resources.description_oikaze
import inflationmemorygame.composeapp.generated.resources.description_plus_one
import inflationmemorygame.composeapp.generated.resources.description_superhuman
import inflationmemorygame.composeapp.generated.resources.description_totteoki
import inflationmemorygame.composeapp.generated.resources.hirameki
import inflationmemorygame.composeapp.generated.resources.hold
import inflationmemorygame.composeapp.generated.resources.name_hirameki
import inflationmemorygame.composeapp.generated.resources.name_hold
import inflationmemorygame.composeapp.generated.resources.name_oikaze
import inflationmemorygame.composeapp.generated.resources.name_plus_one
import inflationmemorygame.composeapp.generated.resources.name_superhuman
import inflationmemorygame.composeapp.generated.resources.name_totteoki
import inflationmemorygame.composeapp.generated.resources.oikaze
import inflationmemorygame.composeapp.generated.resources.plus_one
import inflationmemorygame.composeapp.generated.resources.superhuman
import inflationmemorygame.composeapp.generated.resources.totteoki
import org.bak.inflationmemorygame.values.MaxOccurrence
import org.bak.inflationmemorygame.values.MaxOccurrences
import org.bak.inflationmemorygame.values.Params
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class Abilities(
    override val displayNameRes: StringResource,
    override val descriptionRes: StringResource,
    override val descriptionParams: Array<Any>,
    maxOccurrence: MaxOccurrence,
    override val image: DrawableResource
) : Ability.Implementable {
    PlusOne(
        displayNameRes = Res.string.name_plus_one,
        descriptionRes = Res.string.description_plus_one,
        descriptionParam = 1,
        maxOccurrence = MaxOccurrences.PlusOne,
        image = Res.drawable.plus_one
    ),
    Superhuman(
        displayNameRes = Res.string.name_superhuman,
        descriptionRes = Res.string.description_superhuman,
        descriptionParam = 2,
        maxOccurrence = MaxOccurrences.Superhuman,
        image = Res.drawable.superhuman
    ),
    Oikaze(
        displayNameRes = Res.string.name_oikaze,
        descriptionRes = Res.string.description_oikaze,
        descriptionParam = 1,
        maxOccurrence = MaxOccurrences.Oikaze,
        image = Res.drawable.oikaze
    ),
    Hirameki(
        displayNameRes = Res.string.name_hirameki,
        descriptionRes = Res.string.description_hirameki,
        descriptionParam = Params.HIRAMEKI_DEFAULT_AMOUNT,
        maxOccurrence = MaxOccurrences.Hirameki,
        image = Res.drawable.hirameki
    ),
    Hold(
        displayNameRes = Res.string.name_hold,
        descriptionRes = Res.string.description_hold,
        descriptionParam = 1,
        maxOccurrence = MaxOccurrences.Hold,
        image = Res.drawable.hold
    ),
    Totteoki(
        displayNameRes = Res.string.name_totteoki,
        descriptionRes = Res.string.description_totteoki,
        descriptionParam = Params.TOTTEOKI_MAX,
        maxOccurrence = MaxOccurrences.Totteoki,
        image = Res.drawable.totteoki
    )
    ;

    constructor(
        displayNameRes: StringResource,
        descriptionRes: StringResource,
        descriptionParam: Any,
        maxOccurrence: MaxOccurrence,
        image: DrawableResource
    ) : this(
        displayNameRes = displayNameRes,
        descriptionRes = descriptionRes,
        descriptionParams = arrayOf(descriptionParam),
        maxOccurrence = maxOccurrence,
        image = image
    )

    override val id: Int get() = ordinal
    override val maxOccurrenceInStage: Int = maxOccurrence.inStage
    override val maxLevel: Int = maxOccurrence.inGame
}
