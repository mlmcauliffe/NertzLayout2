package com.example.nertzlayout2

typealias Pile = Pair<PileMgr, PileLayout>

fun Pile.accepts(card: CardMgr): Boolean {
    return first.accepts(card)
}

fun Pile.transfer(card: CardMgr, ncl: CardLayout) {
    first.transfer(card)
    second.transfer(ncl)
}

fun Pile.reposition(startingAt: Int = 0) {
    second.reposition(startingAt)
}

fun Pile.reposition(ncl: CardLayout) {
    second.reposition(ncl)
}

fun Pile.distanceSQ(ncl: CardLayout): Int {
    return second.distanceSQ(ncl)
}

fun Pile.distanceSQ(pile: Pile): Int {
    return second.distanceSQ(pile)
}