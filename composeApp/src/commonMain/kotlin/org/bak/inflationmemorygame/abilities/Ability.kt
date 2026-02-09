package org.bak.inflationmemorygame.abilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Stable
interface Ability {
    val id: Int
    val displayName: String @Composable get
    val description: String @Composable get
    val image: DrawableResource
    val maxOccurrenceInStage: Int
    val level: Int get() = 1
    val maxLevel: Int

    interface Implementable : Ability {
        val displayNameRes: StringResource
        val descriptionRes: StringResource
        val descriptionParams: Array<Any> get() = emptyArray()

        override val displayName: String
            @Composable get() = stringResource(displayNameRes)
        override val description: String
            @Composable get() = stringResource(descriptionRes, *descriptionParams)
    }
}