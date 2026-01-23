package org.bak.inflationmemorygame.values

sealed interface MaxOccurrence {
    val inStage: Int
    val inGame: Int
}

enum class MaxOccurrences(
    override val inStage: Int,
    override val inGame: Int = NO_LIMIT
) : MaxOccurrence {

    PlusOne(inStage = 6),
    Superhuman(inStage = 2)
    ;

    companion object {
        /** 無制限. */
        const val NO_LIMIT = -1
    }
}