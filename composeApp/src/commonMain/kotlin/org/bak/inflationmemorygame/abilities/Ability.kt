package org.bak.inflationmemorygame.abilities

import org.jetbrains.compose.resources.DrawableResource

interface Ability {
    val displayName: String
    val description: String
    val image: DrawableResource
    val maxOccurrenceInStage: Int
    val maxOccurrenceInGame: Int
}