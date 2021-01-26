package com.example.nertzlayout2

import android.view.ViewGroup

class TurnPileLayout(parent: ViewGroup, color: Int, x: Int, y: Int, width: Int, baseHeight: Int)
    : PileLayout(parent, color, x, y, width, baseHeight) {

    val invisibleCards = ArrayList<NertzCardView>()

    companion object {
        val TurnAtATime = 3
    }

    fun allVisible(): Boolean {
        return invisibleCards.isEmpty()
    }

    fun turn() {
        val toTurn = Math.min(invisibleCards.size, TurnAtATime)
        accept(invisibleCards, toTurn)
    }

    fun reset() {
        release(invisibleCards)
    }
}