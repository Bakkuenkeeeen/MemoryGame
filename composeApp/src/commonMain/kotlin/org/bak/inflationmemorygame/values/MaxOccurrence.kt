package org.bak.inflationmemorygame.values

sealed interface MaxOccurrence {
    val inStage: Int
    val inGame: Int
}

enum class MaxOccurrences(
    override val inStage: Int = MAX_IN_STAGE,
    override val inGame: Int = NO_LIMIT_IN_GAME
) : MaxOccurrence {

    PlusOne,
    Superhuman(inStage = 2),
    Oikaze(inStage = 2, inGame = 5),
    Hirameki(inStage = 2),
    ;

    companion object {
        /** 1ステージに出現できる数の最大値. */
        const val MAX_IN_STAGE = 4

        /** 無制限. */
        const val NO_LIMIT_IN_GAME = -1
    }
}