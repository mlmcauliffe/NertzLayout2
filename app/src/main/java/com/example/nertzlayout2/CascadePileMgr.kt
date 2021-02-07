package com.example.nertzlayout2

class CascadePileMgr: PileMgr(true) {

    val suitCompat = mapOf (
            Suit.CLUBS.ordinal    to arrayOf(Suit.DIAMONDS.ordinal, Suit.HEARTS.ordinal),
            Suit.DIAMONDS.ordinal to arrayOf(Suit.CLUBS.ordinal,    Suit.SPADES.ordinal),
            Suit.HEARTS.ordinal   to arrayOf(Suit.CLUBS.ordinal,    Suit.SPADES.ordinal),
            Suit.SPADES.ordinal   to arrayOf(Suit.DIAMONDS.ordinal, Suit.HEARTS.ordinal))

    override fun accepts(card: CardMgr): Boolean {
        val myTop = top ?: return true
        return (suitCompat[myTop.suit] ?: error("")).contains(card.suit) &&
                card.value == myTop.value - 1
    }
}