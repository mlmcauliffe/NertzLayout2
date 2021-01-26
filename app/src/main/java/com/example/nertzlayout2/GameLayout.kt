package com.example.nertzlayout2

import android.content.Context
import android.view.View
import android.view.ViewGroup

class GameLayout(
        val parent: ViewGroup,
        val layout: GameBoardLayout,
        val acePiles: Array<PileLayout>,
        val nertzPile: PileLayout,
        val cascadePiles: Array<PileLayout>,
        val turnPile: TurnPileLayout,
        val hitPileTop: HitPileLayout) {

    companion object {
        const val PileColor = 0xFFBDBDBD.toInt()
    }

    val context: Context get() = parent.context

    fun abovePlayerTop(view: View): Boolean {
        val ypos = view.y + (view.height * 0.1f)
        return ypos < layout.playerTop
    }
}

fun GameLayout(parent: ViewGroup, layout: GameBoardLayout): GameLayout {
    return GameLayout(parent, layout,
        Array<PileLayout>(layout.aceLocations.size) {
            val location = layout.aceLocations[it]
            PileLayout(parent, GameLayout.PileColor,
                    location.x, location.y, layout.aceWidth, layout.aceHeight)
        }, {
            val location = layout.nertzLocation
            PileLayout(parent, GameLayout.PileColor,
                    location.x, location.y, layout.playerWidth, layout.playerHeight)
        }(),
        Array<PileLayout>(layout.cascadeLocations.size) {
            val location = layout.cascadeLocations[it]
            CascadePileLayout(parent, GameLayout.PileColor, location.x, location.y,
                layout.playerWidth, layout.playerHeight, layout.cascadeOverlapSize)
        }, {
            val location = layout.turnLocation
            TurnPileLayout(parent, GameLayout.PileColor,
                    location.x, location.y, layout.playerWidth, layout.playerHeight)
        }(), {
             val location = layout.hitLocation
            HitPileLayout(parent, location.x, location.y, layout.playerWidth, layout.playerHeight)
        }()
    )
}
