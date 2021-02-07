package com.example.nertzlayout2

typealias TurnPile = Pair<TurnPileMgr, TurnPileLayout>

fun TurnPile.allVisible(): Boolean {
    return first.allVisible()
}

fun TurnPile.turn() {
    second.turn(first.turn())
}

fun TurnPile.reset() {
    first.reset()
    second.reset()
}