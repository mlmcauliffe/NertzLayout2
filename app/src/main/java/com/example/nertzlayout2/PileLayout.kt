package com.example.nertzlayout2

import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView

fun View.resize(wi: Int, hi: Int) {
    layoutParams = layoutParams.apply {
        width = wi
        height = hi
    }
}

open class PileLayout(val parent: ViewGroup, color: Int,
                      val x: Int, val y: Int, val width: Int, val height: Int) {

    val cards = arrayListOf<CardLayout>()
    val size: Int get() = cards.size
    open val cardOffset  = 0
    val pileHeight: Int get() = height + verticalOffset(size)

    val top: CardLayout? get() = cards.firstOrNull()

    init {
        val view = CardView(parent.context)
        if (color != 0) {
            view.setCardBackgroundColor(color)
            view.radius = width / CardLayout.radiusDivisor
        }
        parent.addView(view)
        view.resize(width, height)
        positionView(view, x, y)
    }

    fun CardLayout(card: CardMgr): CardLayout {
        return CardLayout(parent, card, this, size).also {
            it.resize(width, height)
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
                it.pile = this
                it.posInPile = size
                cards.add(it)
            }
        }
    }

    fun transfer(firstCard: CardLayout) {
        transfer(firstCard.pile, firstCard.posInPile)
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

    // Given an already-positioned card, correctly position its successors
    private fun positionCards(ncl: CardLayout) {
        var prev = ncl
        for (idx in ncl.posInPile + 1 until size) {
            val curr = cards[idx]
            positionCard(curr, prev)
            prev = curr
        }
    }

    // Move all cards starting at startingAt by the given distanceX and distanceY
    fun move(startingAt: Int, distanceX: Int, distanceY: Int) {
        if (cards.size <= startingAt) return
        val ncl = cards[startingAt]
        ncl.x += distanceX
        ncl.y = Math.max(ncl.y + distanceY, 0f)
        positionCards(ncl)
    }

    // Move all cards starting at startingAt to their correct positions for this pile.
    fun reposition(startingAt: Int = 0) {
        if (cards.size <= startingAt) return
        val ncl = cards[startingAt]
        positionCard(ncl)
        positionCards(ncl)
    }

    fun reposition(ncl: CardLayout) {
        reposition(ncl.posInPile)
    }

    // Calculates the x- and y-distance from the given card's current location to its correction
    // location for this pile.
    fun animateTransfer(firstCard: CardLayout): MoveAnimation {
        return if (firstCard.width == width && firstCard.height == height) {
            MoveAnimation(firstCard.posInPile,
                    firstCard.x.toInt(),
                    firstCard.y.toInt(),
                    x - firstCard.x.toInt(),
                    y + verticalOffset(firstCard.posInPile) - firstCard.y.toInt())
        } else {
            MoveResizeAnimation(firstCard.posInPile,
                    firstCard.x.toInt(), firstCard.y.toInt(),
                    x - firstCard.x.toInt(),
                    y + verticalOffset(firstCard.posInPile) - firstCard.y.toInt(),
                    firstCard.width, firstCard.height,
                    width - firstCard.width, height - firstCard.height)
        }
    }

    open inner class MoveAnimation(val startingAt: Int,
                             val startX: Int, val startY: Int,
                             val distanceX: Int, val distanceY: Int): AnimationOp {
        override fun progress(fraction: Float) {
            progressMove(this, fraction)
        }
    }

    inner class MoveResizeAnimation(cardIdx: Int,
                              startX: Int, startY: Int,
                              distanceX: Int, distanceY: Int,
                              val startWidth: Int, val startHeight: Int,
                              val changeW: Int, var changeH: Int):
            MoveAnimation(cardIdx, startX, startY, distanceX, distanceY)
    {
        override fun progress(fraction: Float) {
            super.progress(fraction)
            progressResize(this, fraction)
        }
    }

    fun progressMove(anim: MoveAnimation, fraction: Float) {
        val ncl = cards[anim.startingAt]
        ncl.x = anim.startX + (fraction * anim.distanceX)
        ncl.y = anim.startY + (fraction * anim.distanceY)
        positionCards(ncl)
    }

    fun progressResize(anim: MoveResizeAnimation, fraction: Float) {
        val ncl = cards[anim.startingAt]
        ncl.resize(anim.startWidth + (anim.changeW * fraction).toInt(),
                anim.startHeight + (anim.changeH * fraction).toInt())
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

    fun acceptAll(fromPile: ArrayList<CardLayout>) {
        accept(fromPile, fromPile.size)
    }

    fun release(toPile: ArrayList<CardLayout>, count: Int) {
        for (idx in 0 until count) {
            cards.removeLast().let {
                parent.removeView(it)
                it.posInPile = toPile.size
                toPile.add(0, it)
            }
        }
    }

    fun releaseAll(toPile: ArrayList<CardLayout>) {
        release(toPile, cards.size)
    }

    // Returns true if the given point falls within the boundaries of this pile given the number
    // of cards in the pile.
    fun contains(view: View): Boolean {
        val px = view.x + (view.width / 2)
        val py = view.y + (view.height * .1f)
        return px >= x && px < x + width && py >= y && py < y + pileHeight
    }

    // Returns the distance in pixels between this.y and the top of a card at the given position
    // in this pile.
    open fun verticalOffset(posInPile: Int): Int {
        return posInPile * cardOffset
    }
}