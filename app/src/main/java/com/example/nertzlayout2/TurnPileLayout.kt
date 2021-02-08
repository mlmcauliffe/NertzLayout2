package com.example.nertzlayout2

import android.view.ViewGroup

class TurnPileLayout(parent: ViewGroup, color: Int, x: Int, y: Int, width: Int, baseHeight: Int)
    : PileLayout(parent, color, x, y, width, baseHeight) {

    val invisibleCards = ArrayList<CardLayout>()

    fun turn(toTurn: Int) {
        accept(invisibleCards, toTurn)
    }

    fun undoTurn(count: Int) {
        release(invisibleCards, count)
    }

    fun reset() {
        releaseAll(invisibleCards)
    }

    fun undoReset() {
        acceptAll(invisibleCards)
    }
}