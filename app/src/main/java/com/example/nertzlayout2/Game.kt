package com.example.nertzlayout2

class Game(
        val acePiles: Array<AcePileMgr>,
        val nertzPile: PileMgr,
        val cascadePiles: Array<CascadePileMgr>,
        val turnPile: TurnPileMgr,
)

fun Game(cascadePileCount: Int): Game {
    return Game(
        Array<AcePileMgr>(Suit.values().size) { AcePileMgr(it) },
        PileMgr(true),
        Array<CascadePileMgr>(cascadePileCount) { CascadePileMgr() },
        TurnPileMgr())
}