package com.example.nertzlayout2

import android.annotation.SuppressLint
import android.util.Log
import android.view.*
import android.widget.Button

@SuppressLint("ClickableViewAccessibility")
class UIPlayer(val gc: GameController, val undoButton: Button) {

    init {
        for (card in gc.cards) {
            card.second.setOnTouchListener(makeCardListener(card.first, card.second))
        }
        gc.hitPileTop.setOnTouchListener(makeHitListener())
        undoButton.setOnClickListener(makeUndoListener())
        setUndoable()
    }

    fun makeCardListener(card: CardMgr, ncl: CardLayout): View.OnTouchListener {
        val listener = FullOnGestureListenerAdapter(object: FullOnGestureListener() {
            var upFling = false
            override fun onDown(e: MotionEvent): Boolean {
                if (!card.pile.isMovable(card)) {
                    return false
                }
                gc.beginUIOperation(ncl)
                ncl.pile.beginMoveOperation(ncl)
                upFling = false
                return true
            }
            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                ncl.pile.move(ncl.posInPile, distanceX.toInt(), distanceY.toInt())
                return true
            }
            override fun onFling(e1: MotionEvent?, e2: MotionEvent?,
                                 velocityX: Float, velocityY: Float): Boolean {
                upFling = (velocityX >= 0 && velocityY < 0 && -velocityY > velocityX)
                return true
            }
            override fun onScrollEnd() {
                ncl.pile.endMoveOperation(ncl)
                val newPile = if ((gc.abovePlayerTop(ncl) || upFling) && card == card.pile.top) {
                    // Choose an ace pile if we have moved above the player piles
                    gc.acePiles[card.suit].let {
                        if (it.first.accepts(card)) it else null
                    }
                } else {
                    // Choose a cascade pile if we can
                    chooseDestination(card, ncl)
                }

                if (newPile == null || newPile.first == card.pile) {
                    gc.reposition(ncl)
                } else {
                    gc.transfer(card, ncl, newPile)
                }
                setUndoable()
            }

            fun chooseDestination(card: CardMgr, ncl: CardLayout): Pile? {
                if (Math.abs(ncl.x - ncl.pile.x.toFloat()) < ncl.width / 3) {
                    // If we have moved less than 1/3 the width of a card, stay put
                    return null
                }
                var ret: Pile? = null
                var minDistance = 0
                for (pile in gc.cascadePiles) {
                    if (!pile.accepts(card)) {
                        continue
                    }
                    val distance = Math.abs(ncl.x.toInt() - pile.second.x)
                    if (ret == null || distance < minDistance) {
                        ret = pile
                        minDistance = distance
                    }
                }
                return ret
            }
        })
        return FullGestureDetector(gc.context, listener)
    }

    fun makeHitListener(): View.OnTouchListener {
        return object: View.OnTouchListener {
            override fun onTouch(p0: View, event: MotionEvent): Boolean {
                if (event.action != MotionEvent.ACTION_DOWN) return false
                gc.hit()
                setUndoable()
                return true
            }
        }
    }

    fun makeUndoListener(): View.OnClickListener {
        return object: View.OnClickListener {
            override fun onClick(button: View?) {
                gc.undo()
                setUndoable()
            }
        }
    }

    fun setUndoable() {
        undoButton.isEnabled = gc.undoable
    }

    fun log(msg: String) {
        Log.d("mlm", msg)
    }
}