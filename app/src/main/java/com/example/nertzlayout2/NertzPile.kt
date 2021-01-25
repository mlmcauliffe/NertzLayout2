package com.example.nertzlayout2

class NertzPile: Pile() {
    override fun accepts(card: NertzCard): Boolean {
        return false
    }

    override val isSource = true
}