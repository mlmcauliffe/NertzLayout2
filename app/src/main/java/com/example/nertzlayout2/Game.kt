package com.example.nertzlayout2

class Game(
        val acePiles: Array<AcePile>,
        val nertzPile: NertzPile,
        val cascadePiles: Array<CascadePile>)

fun Game(cascadePileCount: Int): Game {
    return Game(
        Array<AcePile>(Suit.values().size) { AcePile() },
        NertzPile(),
        Array<CascadePile>(cascadePileCount) { CascadePile() })
}