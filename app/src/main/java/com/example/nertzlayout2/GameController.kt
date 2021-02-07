package com.example.nertzlayout2

import android.content.Context
import android.view.View
import android.widget.Button

typealias Card = Pair<CardMgr, CardLayout>

class GameController(val game: Game, val layout: GameLayout) {
    companion object {
        val NertzPileCards = 13
    }

    val cards = mutableListOf<Card>()
    val nertzPile: Pile
    val acePiles: List<Pile>
    val cascadePiles: List<Pile>
    val turnPile: TurnPile
    val hitPileTop = layout.hitPileTop

    val context: Context get() = layout.context
    var animationInFlight: MoveAnimator? = null
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

    fun beginUIOperation() {
        if (animationInFlight != null) animationInFlight!!.stop()
        animationInFlight = null
    }

    fun setUndo(undo: Undo?) {
        toUndo = undo
    }

    fun hit(): Boolean {
        beginUIOperation()

        if (turnPile.allVisible()) {
            turnPile.reset()
        } else {
            turnPile.turn()
            turnPile.reposition()
        }
        hitPileTop.setEmpty(turnPile.allVisible())
        setUndo(null)
        return false
    }

    fun reposition(ncl: CardLayout) {
        val stage = ncl.pile.stageReposition(ncl)
        val animator = MoveAnimator(stage)
        animator.start()
        animationInFlight = animator
    }

    fun transfer(card: CardMgr, ncl: CardLayout, pile: Pile): Boolean {
        setUndo(CardMoveUndo(card, ncl, Pile(card.pile, ncl.pile)))
        pile.transfer(card, ncl)
        reposition(ncl)
        return true
    }

    fun undo(): Boolean {
        with (toUndo ?: return false) {
            val animator = apply()
            if (animator != null) {
                animator.start()
                animationInFlight = animator
            }
        }
        setUndo(null)
        return false
    }

    class CardMoveUndo(val card: CardMgr, var ncl: CardLayout, val orig: Pile): Undo {
        constructor(card: CardMgr, ncl: CardLayout): this(card, ncl, Pile(card.pile, ncl.pile))

        override fun apply(): MoveAnimator? {
            orig.first.transfer(card)
            orig.second.transfer(ncl)
            val stage = orig.second.stageReposition(ncl)
            return MoveAnimator(stage)
        }
    }
}