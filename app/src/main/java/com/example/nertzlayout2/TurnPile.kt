package com.example.nertzlayout2

typealias TurnPile = Pair<TurnPileMgr, TurnPileLayout>

fun TurnPile.allVisible(): Boolean {
    return first.allVisible()
}

fun TurnPile.turn(): Int {
    val count = first.turn()
    second.turn(count)
    return count
}

fun TurnPile.undoTurn(count: Int) {
    first.undoTurn(count)
    second.undoTurn(count)
}

fun TurnPile.reset() {
    first.reset()
    second.reset()
}

fun TurnPile.undoReset() {
    first.undoReset()
    second.undoReset()
}