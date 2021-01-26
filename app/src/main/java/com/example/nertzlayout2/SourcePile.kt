package com.example.nertzlayout2

open class SourcePile: Pile() {
    override fun accepts(card: NertzCard): Boolean {
        return false
    }

    override val isSource = true
}