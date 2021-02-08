package com.example.nertzlayout2

class TurnPileMgr: PileMgr(true) {
    val invisibleCards = ArrayList<CardMgr>()

    companion object {
        val TurnAtATime = 3
    }

    fun allVisible(): Boolean {
        return invisibleCards.isEmpty()
    }

    fun turn(): Int {
        val toTurn = Math.min(invisibleCards.size, TurnAtATime)
        accept(invisibleCards, toTurn)
        return toTurn
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