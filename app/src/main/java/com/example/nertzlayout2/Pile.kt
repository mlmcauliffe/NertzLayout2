package com.example.nertzlayout2

abstract class Pile {
    val cards = arrayListOf<NertzCard>()

    val isEmpty get() = cards.isEmpty()
    val size get() = cards.size
    val top get() = cards.lastOrNull()

    abstract fun accepts(card: NertzCard): Boolean
    abstract val isSource: Boolean

    fun NertzCard(suit: Suit): NertzCard {
        return NertzCard(suit, this, size).also {
            cards.add(it)
        }
    }

    fun transfer(fromPile: Pile, startingAt: Int) {
        if (BuildConfig.DEBUG && fromPile == this) {
            error("transfer: source and destination piles are the same")
        }
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

    fun accept(fromPile: ArrayList<NertzCard>, count: Int) {
        for (idx in 0 until count) {
            fromPile.removeFirst().let {
                it.posInPile = size
                cards.add(it)
            }
        }
    }

    fun release(toPile: ArrayList<NertzCard>) {
        toPile.addAll(cards)
        cards.clear()
    }
}