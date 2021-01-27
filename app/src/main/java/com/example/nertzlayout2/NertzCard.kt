package com.example.nertzlayout2

class NertzCard(val cardValue: Int, var pile: Pile, var posInPile: Int = 0) {
    val suit get() = cardValue / Deck.CardsPerSuit
    val value get() = cardValue % Deck.CardsPerSuit
}