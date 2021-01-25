package com.example.nertzlayout2

class AcePile: Pile() {
    override fun accepts(card: NertzCard): Boolean {
        val myTop = top ?: return true
        return myTop.suit == card.suit
    }

    override val isSource = false
}