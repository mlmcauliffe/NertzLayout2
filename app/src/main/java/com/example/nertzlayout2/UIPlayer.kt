package com.example.nertzlayout2

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.*

@SuppressLint("ClickableViewAccessibility")
class UIPlayer(val game: Game, val layout: GameLayout) {

    companion object {
        val NertzPileCards = 13
        val TurnAtATime = 3
    }

    var acePiles: List<Pair<Pile, PileLayout>>
    var cascadePiles: List<Pair<Pile, PileLayout>>

    init {
        val dealer = Dealer()

        for (idx in 0 until NertzPileCards) {
            val card = game.nertzPile.NertzCard(dealer.next())
            val ncv = layout.nertzPile.NertzCardView(card)
            ncv.setOnTouchListener(nertzCardListener(card, ncv))
        }
        layout.nertzPile.reposition()

        for (pair in game.cascadePiles.zip(layout.cascadePiles)) {
            val card = pair.first.NertzCard(dealer.next())
            val ncv = pair.second.NertzCardView(card)
            ncv.setOnTouchListener(nertzCardListener(card, ncv))
            pair.second.reposition()
        }

        acePiles = game.acePiles.zip(layout.acePiles)
        cascadePiles = game.cascadePiles.zip(layout.cascadePiles)

        while (dealer.more) {
            val card = game.turnPile.NertzCard(dealer.next())
            val ncv = layout.turnPile.NertzCardView(card)
            ncv.setOnTouchListener(nertzCardListener(card, ncv))
        }
        game.turnPile.reset()
        layout.turnPile.reset()
        layout.turnPile.reposition()

        layout.hitPileTop.apply {
            setOnTouchListener(hitPileListener(layout.hitPileTop, game.turnPile, layout.turnPile))
            bringToFront()
            setEmpty(false)
        }
    }

    val context: Context get() = layout.context
    var animationInFlight: StagedMove? = null

    fun nertzCardListener(card: NertzCard, ncv: NertzCardView): View.OnTouchListener {
        val listener = FullOnGestureListenerAdapter(object: FullOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                if (animationInFlight != null) {
                    // Don't accept UI actions while animation is in progress
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
                    acePiles[card.suit].let {
                        if (it.first.accepts(card)) it else null
                    }
                } else {
                    // Choose a cascade pile if we can
                    cascadePiles.firstOrNull {
                        it.second.contains(ncv) && it.first.accepts(card)
                    }
                }

                val destination = if (newPile == null || newPile.first == card.pile) {
                    ncv.pile
                } else {
                    newPile.first.transfer(card)
                    newPile.second.transfer(ncv)
                    if (!newPile.first.isSource) {
                        ncv.setOnTouchListener(null)
                    }
                    newPile.second
                }
                val stage = destination.stageReposition(ncv)
                animateStagedMove(stage)
            }

            fun animateStagedMove(stage: StagedMove) {
                val animator = ValueAnimator.ofFloat(0f, 1f)
                animator.setDuration(100)
                animator.addUpdateListener(
                        ValueAnimatorAdapter({ fraction ->
                            stage.pile.animateMove(stage.startingAt, (stage.distanceX * fraction).toInt(),
                                    (stage.distanceY * fraction).toInt())
                        }, {
                            stage.pile.reposition(stage.startingAt)
                            animationInFlight = null
                        })
                )
                animationInFlight = stage
                animator.start()
            }
        })
        return FullGestureDetector(context, listener)
    }

    fun hitPileListener(hitPileTop: HitPileLayout, turnPile: TurnPile, turnPileLayout: TurnPileLayout):
        View.OnTouchListener {
        return object: View.OnTouchListener {
            override fun onTouch(p0: View, event: MotionEvent): Boolean {
                if (event.action != MotionEvent.ACTION_DOWN) {
                    return false
                }
                if (turnPile.allVisible()) {
                    turnPile.reset()
                    turnPileLayout.reset()
                } else {
                    turnPile.turn()
                    turnPileLayout.turn()
                    turnPileLayout.reposition()
                }
                hitPileTop.setEmpty(turnPile.allVisible())
                return true
            }
        }
    }

    fun log(msg: String) {
        Log.d("mlm", msg)
    }
}