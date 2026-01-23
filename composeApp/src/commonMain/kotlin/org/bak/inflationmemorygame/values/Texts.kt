package org.bak.inflationmemorygame.values

sealed interface AbilityText {
    val displayName: String
    val description: String
}

enum class AbilityTexts(override val displayName: String, override val description: String) :
    AbilityText {
    PlusOne("プラスワン", "めくれる回数が1回増える。"),
    Superhuman("超人化", "次のターン、めくれる回数が2倍になる。")
    ;
}