package com.example.nertzlayout2

import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView

class StagedMove(val pile: PileLayout, val startingAt: Int,
                 val distanceX: Int, val distanceY: Int)

open class PileLayout(val parent: ViewGroup, color: Int,
                      val x: Int, val y: Int, val width: Int, val baseHeight: Int) {

    val cards = arrayListOf<CardLayout>()
    val size: Int get() = cards.size
    open val cardOffset  = 0
    val fullHeight: Int get() = baseHeight + verticalOffset(size)

    val top: CardLayout? get() = cards.firstOrNull()

    init {
        val view = CardView(parent.context)
        if (color != 0) {
            view.setCardBackgroundColor(color)
            view.radius = width / CardLayout.radiusDivisor
        }
        parent.addView(view)
        resizeView(view)
        positionView(view, x, y)
    }

    fun CardLayout(card: CardMgr): CardLayout {
        return CardLayout(parent, card, this, size).also {
            resizeView(it)
            cards.add(it)
        }
    }

    // Removes cards from fromPile starting with startingAt and adds them to this pile.
    // Resizes the moved cards but does not change their positions.
    fun transfer(fromPile: PileLayout, startingAt: Int) {
        if (BuildConfig.DEBUG && fromPile == this) {
            error("transfer: source and destination piles are the same")
        }
        while (fromPile.size > startingAt) {
            fromPile.cards.removeAt(startingAt).let {
                resizeView(it)
                it.pile = this
                it.posInPile = size
                cards.add(it)
            }
        }
    }

    fun transfer(firstCard: CardLayout) {
        transfer(firstCard.pile, firstCard.posInPile)
    }

    // Resizes a view to this pile's size
    private fun resizeView(view: View) {
        val lp = view.layoutParams
        lp.width = width
        lp.height = baseHeight
        view.layoutParams = lp
    }

    // Moves the given view to (x, y)
    private fun positionView(view: View, x: Int, y: Int) {
        view.x = x.toFloat()
        view.y = y.toFloat()
    }

    // Moves a card to its correct location in this pile
    private fun positionCard(ncl: CardLayout) {
        positionView(ncl, x, y + verticalOffset(ncl.posInPile))
    }

    // Moves a card to its correct location relative to its predecessor
    private fun positionCard(ncl: CardLayout, pred: CardLayout) {
        positionView(ncl, pred.x.toInt(), pred.y.toInt() + cardOffset)
    }

    // Move all cards starting at startingAt by the given distanceX and distanceY
    fun move(startingAt: Int, distanceX: Int, distanceY: Int) {
        for (idx in startingAt until size) {
            val ncl = cards[idx]
            ncl.x += distanceX
            ncl.y += distanceY
        }
    }

    // Move all cards starting at startingAt to their correct positions for this pile.
    fun reposition(startingAt: Int = 0) {
        if (cards.isEmpty()) return
        var prev = cards[startingAt]
        positionCard(prev)
        for (idx in startingAt + 1 until size) {
            val ncl = cards[idx]
            positionCard(ncl, prev)
            prev = ncl
        }
    }

    fun reposition(ncl: CardLayout) {
        reposition(ncl.posInPile)
    }

    // Calculates the x- and y-distance from the given card's current location to its correction
    // location for this pile.
    fun stageReposition(firstCard: CardLayout): StagedMove {
        for (idx in firstCard.posInPile until size) {
            cards[idx].apply {
                animationStartX = x.toInt()
                animationStartY = y.toInt()
            }
        }
        return StagedMove(this, firstCard.posInPile,
                x - firstCard.x.toInt(),
                y + verticalOffset(firstCard.posInPile) - firstCard.y.toInt())
    }

    // Move all cards starting at startingAt by the given distanceX and distanceY
    fun animateMove(startingAt: Int, distanceX: Int, distanceY: Int) {
        for (idx in startingAt until size) {
            val ncl = cards[idx]
            ncl.x = (ncl.animationStartX + distanceX).toFloat()
            ncl.y = (ncl.animationStartY + distanceY).toFloat()
        }
    }

    // Raises the cards starting at startingAt in the view hierarchy
    fun raise(startingAt: Int) {
        for (idx in startingAt until size) {
            cards[idx].bringToFront()
        }
    }

    fun accept(fromPile: ArrayList<CardLayout>, count: Int) {
        for (idx in 0 until count) {
            fromPile.removeFirst().let {
                parent.addView(it)
                it.posInPile = size
                cards.add(it)
            }
        }
    }

    fun release(toPile: ArrayList<CardLayout>) {
        for (card in cards) parent.removeView(card)
        toPile.addAll(cards)
        cards.clear()
    }

    // Returns true if the given point falls within the boundaries of this pile given the number
    // of cards in the pile.
    fun contains(view: View): Boolean {
        val px = view.x + (view.width / 2)
        val py = view.y + (view.height * .1f)
        return px >= x && px < x + width && py >= y && py < y + fullHeight
    }

    // Returns the distance in pixels between this.y and the top of a card at the given position
    // in this pile.
    open fun verticalOffset(posInPile: Int): Int {
        return posInPile * cardOffset
    }
}