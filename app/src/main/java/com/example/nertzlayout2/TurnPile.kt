package com.example.nertzlayout2

typealias TurnPile = Pair<TurnPileMgr, TurnPileLayout>

fun TurnPile.allVisible(): Boolean {
    return first.allVisible()
}

fun TurnPile.turn() {
    first.turn()
    second.turn()
}

fun TurnPile.reset() {
    first.reset()
    second.reset()
}