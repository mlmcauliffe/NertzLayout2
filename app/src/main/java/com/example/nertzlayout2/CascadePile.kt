package com.example.nertzlayout2

class CascadePile: Pile() {
    override fun accepts(card: NertzCard): Boolean {
        val myTop = top ?: return true
        return SuitColors[myTop.suit] != SuitColors[card.suit] &&
                card.value == myTop.value - 1
    }

    override val isSource = true
}