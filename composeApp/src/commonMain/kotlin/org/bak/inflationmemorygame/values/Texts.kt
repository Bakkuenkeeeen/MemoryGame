package org.bak.inflationmemorygame.values

sealed interface AbilityText {
    val displayName: String
    val description: String
}

enum class AbilityTexts(override val displayName: String, override val description: String) :
    AbilityText {
    PlusOne("プラスワン", "めくれる回数が1回増える。"),
    Superhuman("超人化", "次のターン、めくれる回数が2倍になる。"),
    Oikaze(
        "追い風",
        "カードを揃えるたび、そのターン中にめくれる回数が${Params.OIKAZE_AMOUNT}回増える。"
    ),
    Hirameki(
        "ひらめき",
        "次にめくるカードの相方の位置が分かる。"
    ),
    Hold(
        "ホールド",
        "表になっているカードのうち1枚が、ターン終了後も表のまま残る。"
    ),
    Totteoki(
        "とっておき",
        "このカードは、表のまま残り続ける。\n" +
                "揃えるまでに終了したターン数に応じて、最大${Params.TOTTEOKI_MAX}回まで、めくれる回数が増える。\n" +
                "(このカードをめくったターンは数えない。)"
    )
    ;
}