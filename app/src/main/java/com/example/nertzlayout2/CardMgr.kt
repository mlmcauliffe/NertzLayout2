package com.example.nertzlayout2

class CardMgr(val cardValue: Int, var pile: PileMgr, var posInPile: Int = 0) {
    val suit get() = cardValue / Deck.CardsPerSuit
    val value get() = cardValue % Deck.CardsPerSuit
}