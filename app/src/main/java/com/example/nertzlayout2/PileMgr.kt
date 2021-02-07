package com.example.nertzlayout2

open class PileMgr(val isSource: Boolean) {
    val cards = arrayListOf<CardMgr>()

    val isEmpty get() = cards.isEmpty()
    val size get() = cards.size
    val top get() = cards.lastOrNull()

    open fun accepts(card: CardMgr): Boolean {
        return !isSource
    }

    fun CardMgr(cardValue: Int): CardMgr {
        return CardMgr(cardValue, this, size).also {
            cards.add(it)
        }
    }

    fun transfer(fromPile: PileMgr, startingAt: Int) {
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

    fun transfer(firstCard: CardMgr) {
        transfer(firstCard.pile, firstCard.posInPile)
    }

    fun accept(fromPile: ArrayList<CardMgr>, count: Int) {
        for (idx in 0 until count) {
            fromPile.removeFirst().let {
                it.posInPile = size
                cards.add(it)
            }
        }
    }

    fun release(toPile: ArrayList<CardMgr>) {
        toPile.addAll(cards)
        cards.clear()
    }
}