package com.example.nertzlayout2

typealias Pile = Pair<PileMgr, PileLayout>

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