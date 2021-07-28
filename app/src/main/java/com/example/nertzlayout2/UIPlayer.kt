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
            var ptrStartX = 0
            var ptrStartY = 0
            var ptrEndX = 0
            var ptrEndY = 0
            override fun onDown(e: MotionEvent): Boolean {
                if (!card.pile.isMovable(card)) {
                    return false
                }
                gc.beginUIOperation(ncl)
                ncl.pile.beginMoveOperation(ncl)
                ptrStartX = e.rawX.toInt()
                ptrStartY = e.rawY.toInt()

                // set ptrEndX/Y in case onScroll is never called (i.e., touch and release)
                ptrEndX = ptrStartX
                ptrEndY = ptrStartY
                return true
            }
            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                ptrEndX = e2.rawX.toInt()
                ptrEndY = e2.rawY.toInt()
                ncl.pile.move(ncl.posInPile, distanceX.toInt(), distanceY.toInt())
                return true
            }
            override fun onScrollEnd() {
                ncl.pile.endMoveOperation(ncl)
                val destPile = chooseDestPile(card, ncl)
                if (destPile == null || destPile.first == card.pile) {
                    // No new pile accepted card or card was moved to its original pile
                    gc.reposition(ncl)
                } else {
                    // Move card to its new destination
                    gc.transfer(card, ncl, destPile)
                }
                setUndoable()
            }
            fun chooseDestPile(card: CardMgr, ncl: CardLayout): Pile? {
                // If the card didn't move at least 1/4 of its width/height, leave it alone
                val movedX = Math.abs(ptrEndX - ptrStartX)
                val movedY = Math.abs(ptrEndY - ptrStartY)
                val thresholdX = ncl.width / 4
                val thresholdY = ncl.height / 4
                if (movedX < thresholdX && movedY < thresholdY) {
                    return null
                }
                var ret: Pile? = null
                var minDistance = 0
                for (pile in gc.cascadePiles) {
                    if (!pile.accepts(card)) {
                        // (this check also means we don't have to explicitly check whether pile is
                        // card's originating pile -- a pile never accepts its own top card)
                        continue
                    }
                    val distance = pile.distanceSQ(ncl)
                    val origDistance = ncl.pile.distanceSQ(pile)
                    if (distance > origDistance) {
                        // if the card is now farther away from pile than it began, skip the pile
                        continue
                    }
                    // find the closest pile to our new location
                    if (ret == null || distance < minDistance) {
                        ret = pile
                        minDistance = distance
                    }
                }

                if (card.pile.top == card) {
                    gc.acePiles[card.suit].let {
                        if (it.accepts(card)) {
                            val distance = it.distanceSQ(ncl)
                            val origDistance = ncl.pile.distanceSQ(it)
                            if (distance <= origDistance &&
                                (ret == null || distance < minDistance || gc.abovePlayerTop(ncl))) {
                                ret = it
                                minDistance = distance
                            }
                        }
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