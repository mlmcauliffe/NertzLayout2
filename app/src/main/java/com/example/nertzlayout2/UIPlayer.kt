package com.example.nertzlayout2

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.Button

@SuppressLint("ClickableViewAccessibility")
class UIPlayer(val game: Game, val layout: GameLayout, val undoButton: Button) {

    companion object {
        val NertzPileCards = 13
        val TurnAtATime = 3
    }

    var acePiles: List<PilePair>
    var cascadePiles: List<PilePair>
    val context: Context get() = layout.context
    var animationInFlight: MoveAnimator? = null
    var undo: Undo? = null

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

        initUndo(undoButton)
    }

    fun nertzCardListener(card: NertzCard, ncv: NertzCardView): View.OnTouchListener {
        val listener = FullOnGestureListenerAdapter(object: FullOnGestureListener() {
            var upFling = false
            override fun onDown(e: MotionEvent): Boolean {
                if (animationInFlight != null) {
                    animationInFlight!!.stop()
                    animationInFlight = null
                }
                if (!card.pile.isSource) {
                    return false
                }
                ncv.pile.raise(ncv.posInPile)
                upFling = false
                return true
            }
            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                ncv.pile.move(ncv.posInPile, distanceX.toInt(), distanceY.toInt())
                return true
            }
            override fun onFling(e1: MotionEvent?, e2: MotionEvent?,
                                 velocityX: Float, velocityY: Float): Boolean {
                upFling = (velocityX >= 0 && velocityY < 0 && -velocityY > velocityX)
                return true
            }
            override fun onScrollEnd() {
                val newPile = if ((layout.abovePlayerTop(ncv) || upFling) && card == card.pile.top) {
                    // Choose an ace pile if we have moved above the player piles
                    acePiles[card.suit].let {
                        if (it.first.accepts(card)) it else null
                    }
                } else {
                    // Choose a cascade pile if we can
                    chooseDestination(card, ncv)
                }

                val destination = if (newPile == null || newPile.first == card.pile) {
                    ncv.pile
                } else {
                    undo = CardMoveUndo(card, ncv, PilePair(card.pile, ncv.pile))
                    newPile.first.transfer(card)
                    newPile.second.transfer(ncv)
                    newPile.second
                }
                undoButton.isEnabled = (undo != null)
                val stage = destination.stageReposition(ncv)
                val animator = MoveAnimator(stage)
                animator.start()
                animationInFlight = animator
            }

            fun chooseDestination(card: NertzCard, ncv: NertzCardView): PilePair? {
                if (ncv.x + ncv.width / 2 < layout.layout.playerWidth) {
                    return null
                }
                var ret: PilePair? = null
                var minDistance = 0
                for (pair in cascadePiles) {
                    if (!pair.first.accepts(card)) {
                        continue
                    }
                    val distance = Math.abs(ncv.x.toInt() - pair.second.x)
                    if (ret == null || distance < minDistance) {
                        ret = pair
                        minDistance = distance
                    }
                }
                return ret
            }
        })
        return FullGestureDetector(context, listener)
    }

    class CardMoveUndo(val card: NertzCard, var ncv: NertzCardView, val orig: PilePair): Undo {
        constructor(card: NertzCard, ncv: NertzCardView): this(card, ncv, PilePair(card.pile, ncv.pile))

        override fun apply(): MoveAnimator? {
            orig.first.transfer(card)
            orig.second.transfer(ncv)
            val stage = orig.second.stageReposition(ncv)
            return MoveAnimator(stage)
        }
    }

    fun hitPileListener(hitPileTop: HitPileLayout, turnPile: TurnPile, turnPileLayout: TurnPileLayout):
        View.OnTouchListener {
        return object: View.OnTouchListener {
            override fun onTouch(p0: View, event: MotionEvent): Boolean {
                if (event.action != MotionEvent.ACTION_DOWN) {
                    return false
                }
                if (animationInFlight != null) {
                    animationInFlight!!.stop()
                    animationInFlight = null
                }
                undo = null
                undoButton.isEnabled = false

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

    fun initUndo(undoButton: Button) {
        undoButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(button: View?) {
                if (animationInFlight != null) {
                    animationInFlight!!.stop()
                    animationInFlight = null
                }
                with (undo ?: return) {
                    undo = null
                    undoButton.isEnabled = false
                    val animator = apply()
                    if (animator != null) {
                        animator.start()
                        animationInFlight = animator
                    }
                }
            }
        })

        undoButton.isEnabled = false
    }

    fun log(msg: String) {
        Log.d("mlm", msg)
    }
}