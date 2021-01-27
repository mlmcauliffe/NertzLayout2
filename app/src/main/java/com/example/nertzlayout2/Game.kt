package com.example.nertzlayout2

class Game(
        val acePiles: Array<AcePile>,
        val nertzPile: SourcePile,
        val cascadePiles: Array<CascadePile>,
        val turnPile: TurnPile,
)

fun Game(cascadePileCount: Int): Game {
    return Game(
        Array<AcePile>(Suit.values().size) { AcePile(it) },
        SourcePile(),
        Array<CascadePile>(cascadePileCount) { CascadePile() },
        TurnPile())
}