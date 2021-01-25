package com.example.nertzlayout2

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout

@SuppressLint("ClickableViewAccessibility")
class UIPlayer(val game: Game, val layout: GameLayout) {

    var acePiles: List<Pair<Pile, PileLayout>>
    var cascadePiles: List<Pair<Pile, PileLayout>>

    init {
        for (suit in Suit.values()) {
            val card = game.nertzPile.NertzCard(suit)
            val ncv = layout.nertzPile.NertzCardView(card)
            ncv.setOnTouchListener(listener(card, ncv))
        }
        layout.nertzPile.reposition()

        for (pair in game.cascadePiles.zip(layout.cascadePiles)) {
            for (suit in Suit.values()) {
                val card = pair.first.NertzCard(suit)
                val ncv = pair.second.NertzCardView(card)
                ncv.setOnTouchListener(listener(card, ncv))
            }
            pair.second.reposition()
        }

        acePiles = game.acePiles.zip(layout.acePiles)
        cascadePiles = game.cascadePiles.zip(layout.cascadePiles)
    }

    val context: Context get() = layout.context
    var stage = StagedMove()
    var animationInProgress = false

    fun listener(card: NertzCard, ncv: NertzCardView): View.OnTouchListener {
        val listener = FullOnGestureListenerAdapter(object: FullOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                if (animationInProgress) {
                    return false
                }
                ncv.pile.raise(ncv.posInPile)
                return true
            }
            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                ncv.pile.move(ncv.posInPile, distanceX.toInt(), distanceY.toInt())
                return true
            }
            override fun onScrollEnd() {
                val newPile = if (layout.abovePlayerTop(ncv) && card == card.pile.top) {
                    // Choose an ace pile if we have moved above the player piles
                    acePiles[card.suit.ordinal]
                } else {
                    // Choose a cascade pile if we can
                    cascadePiles.firstOrNull {
                        it.second.contains(ncv) && it.first.accepts(card)
                    }
                }

                val destination = if (newPile == null) {
                    ncv.pile
                } else {
                    newPile.first.transfer(card)
                    newPile.second.transfer(ncv)
                    if (!newPile.first.isSource) {
                        ncv.setOnTouchListener(null)
                    }
                    newPile.second
                }
                destination.reposition(ncv)
            }
        })
        return FullGestureDetector(context, listener)
    }

    fun animateStagedMove(pile: PileLayout, startingAt: Int, stage: StagedMove) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.setDuration(2000)
        animator.addUpdateListener(
            ValueAnimatorAdapter({ fraction ->
                pile.move(startingAt, (stage.distanceX * fraction).toInt(),
                        (stage.distanceY * fraction).toInt())
            }, {
                pile.reposition(startingAt)
                animationInProgress = false
            })
        )
        animationInProgress = true
        animator.start()
    }

    fun log(msg: String) {
        Log.d("mlm", msg)
    }
}