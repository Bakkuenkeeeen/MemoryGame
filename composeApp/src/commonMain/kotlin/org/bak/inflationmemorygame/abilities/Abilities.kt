package org.bak.inflationmemorygame.abilities

import androidx.compose.runtime.Composable
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
import org.bak.inflationmemorygame.util.applyInnerAttributes
import org.bak.inflationmemorygame.values.MaxOccurrence
import org.bak.inflationmemorygame.values.MaxOccurrences
import org.bak.inflationmemorygame.values.Params
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

enum class Abilities(
    private val displayNameRes: StringResource,
    private val descriptionBuilder: @Composable () -> String,
    maxOccurrence: MaxOccurrence,
    override val image: DrawableResource
) : Ability {
    PlusOne(
        displayNameRes = Res.string.name_plus_one,
        descriptionBuilder = { stringResource(Res.string.description_plus_one, 1) },
        maxOccurrence = MaxOccurrences.PlusOne,
        image = Res.drawable.plus_one
    ),
    Superhuman(
        displayNameRes = Res.string.name_superhuman,
        descriptionBuilder = { stringResource(Res.string.description_superhuman, 2) },
        maxOccurrence = MaxOccurrences.Superhuman,
        image = Res.drawable.superhuman
    ),
    Oikaze(
        displayNameRes = Res.string.name_oikaze,
        descriptionBuilder = { stringResource(Res.string.description_oikaze, 1) },
        maxOccurrence = MaxOccurrences.Oikaze,
        image = Res.drawable.oikaze
    ),
    Hirameki(
        displayNameRes = Res.string.name_hirameki,
        descriptionBuilder = {
            stringResource(Res.string.description_hirameki, Params.HIRAMEKI_DEFAULT_AMOUNT)
        },
        maxOccurrence = MaxOccurrences.Hirameki,
        image = Res.drawable.hirameki
    ),
    Hold(
        displayNameRes = Res.string.name_hold,
        descriptionBuilder = { stringResource(Res.string.description_hold, 1) },
        maxOccurrence = MaxOccurrences.Hold,
        image = Res.drawable.hold
    ),
    Totteoki(
        displayNameRes = Res.string.name_totteoki,
        descriptionBuilder = {
            stringResource(Res.string.description_totteoki, Params.TOTTEOKI_MAX)
        },
        maxOccurrence = MaxOccurrences.Totteoki,
        image = Res.drawable.totteoki
    )
    ;

    constructor(
        displayNameRes: StringResource,
        descriptionRes: StringResource,
        maxOccurrence: MaxOccurrence,
        image: DrawableResource
    ) : this(
        displayNameRes = displayNameRes,
        descriptionBuilder = { stringResource(descriptionRes) },
        maxOccurrence = maxOccurrence,
        image = image
    )

    override val id: Int get() = ordinal
    override val displayName: String @Composable get() = stringResource(displayNameRes)
    override val description: String @Composable get() = descriptionBuilder()
    override val maxOccurrenceInStage: Int = maxOccurrence.inStage
    override val maxLevel: Int = maxOccurrence.inGame
}
