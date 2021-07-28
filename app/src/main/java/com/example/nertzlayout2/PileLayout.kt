package com.example.nertzlayout2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView

fun View.resize(wi: Int, hi: Int) {
    layoutParams = layoutParams.apply {
        width = wi
        height = hi
    }
}

open class PileLayout(val parent: ViewGroup,
                      val x: Int, val y: Int, val width: Int, val height: Int,
                      private val cardOffset: Int, private val movingCardRaise: Int,
                      base: CardView) {

    companion object {
        fun createBaseView(context: Context, color: Int): CardView {
            return CardView(context).also { it.setCardBackgroundColor(color) }
        }
    }

    constructor(parent: ViewGroup, color: Int, x: Int, y: Int, width: Int, height: Int):
            this(parent, x, y, width, height, 0, 0,
            createBaseView(parent.context, color))

    constructor(parent: ViewGroup, color: Int, x: Int, y: Int, width: Int, height: Int,
                cardOffset: Int, movingCardRaise: Int):
            this(parent, x, y, width, height, cardOffset, movingCardRaise,
            createBaseView(parent.context, color))

    constructor(parent: ViewGroup, x: Int, y: Int, width: Int, height: Int, base: CardView):
            this(parent, x, y, width, height, 0, 0, base)

    private val cards = arrayListOf<CardLayout>()
    private val size: Int get() = cards.size

    init {
        base.radius = width / CardLayout.radiusDivisor
        parent.addView(base)
        base.resize(width, height)
        positionView(base, x, y)
    }

    fun CardLayout(card: CardMgr): CardLayout {
        return CardLayout(parent, card, this, size).also {
            it.resize(width, height)
            cards.add(it)
        }
    }

    // Transfers cards from fromPile to this pile, starting with startingAt.
    // Does not move or resize the transferred cards.
    private fun transfer(fromPile: PileLayout, startingAt: Int) {
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

    // Transfers the given card and the cards below it from their current pile to this pile.
    // Does not move or resize the transferred cards.
    fun transfer(firstCard: CardLayout) {
        transfer(firstCard.pile, firstCard.posInPile)
    }

    // Moves the given view to (x, y)
    private fun positionView(view: View, x: Int, y: Int) {
        view.x = x.toFloat()
        view.y = y.toFloat()
    }

    // Moves a card to its correct location for this pile. Applies the card's yOffset.
    private fun positionCard(ncl: CardLayout) {
        positionView(ncl, x, y + verticalOffset(ncl.posInPile) + ncl.yOffset)
    }

    // Moves a card to its correct location relative to its predecessor. Compensates for the
    // predecessor's yOffset, thereby placing this card as if its predecessor had a yOffset of 0.
    private fun positionCard(ncl: CardLayout, pred: CardLayout) {
        positionView(ncl, pred.x.toInt(), pred.y.toInt() - pred.yOffset + ncl.yOffset + cardOffset)
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

    fun beginMoveOperation(ncl: CardLayout) {
        ncl.yOffset = -movingCardRaise
        positionCard(ncl)
        raise(ncl.posInPile)
    }

    fun endMoveOperation(ncl: CardLayout) {
        ncl.yOffset = 0
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

    // Creates a MoveAnimation object that describes the change in position, and perhaps in
    // size, between the given card's current position and its correct position for this pile.
    fun animateTransfer(topCard: CardLayout): MoveAnimation {
        val currX = topCard.x.toInt()
        val currY = topCard.y.toInt()
        val distanceX = x - currX
        val distanceY = y + verticalOffset(topCard.posInPile) - currY
        val totalDistance = Math.abs((distanceX + distanceY).toFloat() / width.toFloat())
        return if (topCard.width == width && topCard.height == height) {
            MoveAnimation(topCard.posInPile, currX, currY, distanceX, distanceY, totalDistance)
        } else {
            val diffW = width - topCard.width
            val diffH = height - topCard.height
            MoveResizeAnimation(topCard.posInPile, currX, currY, distanceX, distanceY, totalDistance,
                    topCard.width, topCard.height, diffW, diffH)
        }
    }

    open inner class MoveAnimation(val startingAt: Int, val startX: Int, val startY: Int,
                                   val distanceX: Int, val distanceY: Int,
                                   override val totalDistance: Float): AnimationOp {
        override fun progress(fraction: Float) {
            progressMove(this, fraction)
        }
    }

    inner class MoveResizeAnimation(cardIdx: Int, startX: Int, startY: Int,
                                    distanceX: Int, distanceY: Int, totalDistance: Float,
                                    val startWidth: Int, val startHeight: Int,
                                    val changeW: Int, var changeH: Int):
            MoveAnimation(cardIdx, startX, startY, distanceX, distanceY, totalDistance)
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

    // Returns the distance in pixels between this.y and the top of a card at the given position
    // in this pile.
    open fun verticalOffset(posInPile: Int): Int {
        return posInPile * cardOffset
    }

    // Returns the square of the distance from the given card to this pile
    fun distanceSQ(ncl: CardLayout): Int {
        return distanceSQ(x, ncl.x.toInt(),
            y + verticalOffset(size), ncl.y.toInt())
    }

    // Returns the square of the distance between this pile and the given pile
    fun distanceSQ(pile: PileLayout): Int {
        return distanceSQ(x, pile.x,
            y + verticalOffset(size), pile.y + pile.verticalOffset(pile.size))
    }

    fun distanceSQ(pile: Pile): Int {
        return distanceSQ(pile.second)
    }
}