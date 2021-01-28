package com.example.nertzlayout2

enum class Suit {
    CLUBS,
    DIAMONDS,
    HEARTS,
    SPADES
}

val SuitStrings = arrayOf("\u2663", "\u2666", "\u2665", "\u2660")
val CardValueStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

data class CardValue(val suit: Suit, val value: Int)

class Deck {
    companion object {
        val CardsPerSuit = CardValueStrings.size
        val CardsPerDeck = CardsPerSuit * Suit.values().size
    }

    val cards = IntArray(CardsPerDeck) { it } .also { it.shuffle() }

    operator fun get(idx: Int): Int { return cards[idx] }
}

class Dealer {
    private val deck = Deck()
    private var idx = 0
    val more get() = idx < Deck.CardsPerDeck

    fun next(): Int {
        return deck[idx++]
    }
}