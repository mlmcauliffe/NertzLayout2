package com.example.nertzlayout2

enum class Suit {
    CLUBS,
    DIAMONDS,
    HEARTS,
    SPADES
}

class NertzCard(val suit: Suit, var pile: Pile, var posInPile: Int = 0) {
    companion object {
        val cardsPerSuit = 13
        val totalCards = cardsPerSuit * Suit.values().size
    }
}