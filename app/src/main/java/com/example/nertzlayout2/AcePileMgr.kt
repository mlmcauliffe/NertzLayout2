package com.example.nertzlayout2

class AcePileMgr(val suit: Int): PileMgr(false) {
    override fun accepts(card: CardMgr): Boolean {
        if (card.suit != suit) {
            return false
        }
        val myTop = top ?: return card.value == 0
        return card.value == myTop.value + 1
    }
}