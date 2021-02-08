package com.example.nertzlayout2

import android.content.Context
import android.view.View

typealias Card = Pair<CardMgr, CardLayout>

class GameController(val game: Game, val layout: GameLayout) {
    companion object {
        val NertzPileCards = 13
    }

    interface Undo {
        fun apply()
    }

    val cards = mutableListOf<Card>()
    val nertzPile: Pile
    val acePiles: List<Pile>
    val cascadePiles: List<Pile>
    val turnPile: TurnPile
    val hitPileTop = layout.hitPileTop

    val context: Context get() = layout.context

    var cardInFlight: CardLayout? = null
    var animationInFlight: CardAnimator? = null
    var toUndo: Undo? = null

    init {
        val dealer = Dealer()
        val deal = { pile: Pile ->
            val mgr = pile.first.CardMgr(dealer.next())
            val ncl = pile.second.CardLayout(mgr)
            Card(mgr, ncl).also {
                cards.add(it)
            }
        }

        nertzPile = Pile(game.nertzPile, layout.nertzPile)
        acePiles = game.acePiles.zip(layout.acePiles)
        cascadePiles = game.cascadePiles.zip(layout.cascadePiles)
        turnPile = TurnPile(game.turnPile, layout.turnPile)

        for (idx in 0 until NertzPileCards) {
            deal(nertzPile)
        }
        layout.nertzPile.reposition()

        for (pile in cascadePiles) {
            deal(pile)
            pile.second.reposition()
        }

        while (dealer.more) {
            deal(turnPile)
        }
        game.turnPile.reset()
        layout.turnPile.reset()
        layout.turnPile.reposition()

        hitPileTop.apply {
            bringToFront()
            setEmpty(false)
        }
    }

    fun abovePlayerTop(view: View): Boolean {
        return layout.abovePlayerTop(view)
    }

    fun setUndo(undo: Undo?) {
        toUndo = undo
    }

    val undoable: Boolean get() = toUndo != null

    fun beginUIOperation(ncl: CardLayout?) {
        val anim = animationInFlight
        animationInFlight = null
        if (anim != null) {
            if (ncl == cardInFlight) {
                // If the user caught the current in-flight card, stop the animation and allow
                // them to continue their move
                anim.stop()
            } else {
                // If the user touched a different card than the one in flight, finish the
                // animation so they can proceed to the next operation
                anim.finish()
            }
        }
        cardInFlight = ncl
    }

    fun reposition(ncl: CardLayout) {
        val stage = ncl.pile.animateTransfer(ncl)
        val animator = CardAnimator(stage)
        animator.start()
        animationInFlight = animator
    }

    fun transfer(card: CardMgr, ncl: CardLayout, pile: Pile) {
        beginUIOperation(ncl)
        setUndo(UndoTransfer(card, ncl, Pile(card.pile, ncl.pile)))
        pile.transfer(card, ncl)
        reposition(ncl)
    }

    fun hit() {
        beginUIOperation(null)

        if (turnPile.allVisible()) {
            turnPile.reset()
            setUndo(UndoReset())
        } else {
            val count = turnPile.turn()
            turnPile.reposition()
            setUndo(UndoHit(count))
        }
        hitPileTop.setEmpty(turnPile.allVisible())
    }

    fun undoReset() {
        beginUIOperation(null)
        turnPile.undoReset()
        hitPileTop.setEmpty(turnPile.allVisible())
    }

    fun undoHit(count: Int) {
        beginUIOperation(null)
        turnPile.undoTurn(count)
        hitPileTop.setEmpty(turnPile.allVisible())
    }

    fun undo() {
        val undo = toUndo
        setUndo(null)
        if (undo != null) {
            undo.apply()
        }
    }

    inner class UndoTransfer(val card: CardMgr, var ncl: CardLayout, val orig: Pile): Undo {
        constructor(card: CardMgr, ncl: CardLayout): this(card, ncl, Pile(card.pile, ncl.pile))

        override fun apply() {
            transfer(card, ncl, orig)
        }
    }

    inner class UndoReset(): Undo {
        override fun apply() {
            undoReset()
        }
    }
    inner class UndoHit(val count: Int): Undo {
        override fun apply() {
            undoHit(count)
        }
    }
}