package com.example.nertzlayout2

abstract class Pile {
    val cards = arrayListOf<NertzCard>()

    val empty get() = cards.isEmpty()
    val size get() = cards.size
    val top get() = cards.lastOrNull()
    val bottom get() = cards.firstOrNull()

    abstract fun accepts(card: NertzCard): Boolean
    abstract val isSource: Boolean

    fun NertzCard(suit: Suit): NertzCard {
        return NertzCard(suit, this, size).also {
            cards.add(it)
        }
    }

    fun transfer(fromPile: Pile, startingAt: Int) {
        while (fromPile.size > startingAt) {
            fromPile.cards.removeAt(startingAt).let {
                it.pile = this
                it.posInPile = size
                cards.add(it)
            }
        }
    }

    fun transfer(firstCard: NertzCard) {
        transfer(firstCard.pile, firstCard.posInPile)
    }
}