package org.bak.inflationmemorygame.values

sealed interface MaxOccurrence {
    val inStage: Int
    val inGame: Int
}

enum class MaxOccurrences(
    override val inStage: Int = MAX_IN_STAGE,
    override val inGame: Int = NO_LIMIT_IN_GAME
) : MaxOccurrence {

    PlusOne(inGame = 8), // プラスワンだけで10回まで増える
    Superhuman(inStage = 2),
    Oikaze(inStage = 2, inGame = 5),
    Hirameki(inStage = 2),
    Hold(inStage = 2, inGame = 3),
    Totteoki(inStage = 1)
    ;

    companion object {
        /** 1ステージに出現できる数の最大値. */
        const val MAX_IN_STAGE = 4

        /** 無制限. */
        const val NO_LIMIT_IN_GAME = -1
    }
}