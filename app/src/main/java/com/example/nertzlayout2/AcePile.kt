package com.example.nertzlayout2

class AcePile(val suit: Int): Pile() {
    override fun accepts(card: NertzCard): Boolean {
        if (card.suit != suit) {
            return false
        }
        val myTop = top ?: return card.value == 0
        return card.value == myTop.value + 1
    }

    override val isSource = false
}