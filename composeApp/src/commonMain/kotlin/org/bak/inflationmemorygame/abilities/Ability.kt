package org.bak.inflationmemorygame.abilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.DrawableResource

@Stable
interface Ability {
    val id: Int
    val displayName: String @Composable get
    val description: String @Composable get
    val image: DrawableResource
    val maxOccurrenceInStage: Int
    val level: Int get() = 1
    val maxLevel: Int
}