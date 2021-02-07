package com.example.nertzlayout2

import android.view.ViewGroup
import androidx.cardview.widget.CardView

class HitPileLayout(parent: ViewGroup, x: Int, y: Int, width: Int, height: Int):
    CardView(parent.context) {

    companion object {
        val EmptyColor = GameLayout.PileColor
        val NonEmptyColor = 0xFF4CAF50.toInt()
    }

    init {
        this.x = x.toFloat()
        this.y = y.toFloat()
        radius = CardLayout.radiusDivisor
        parent.addView(this)
        val lp = layoutParams
        lp.width = width
        lp.height = height
        layoutParams = lp
    }

    fun setEmpty(empty: Boolean) {
        setCardBackgroundColor(if (empty) EmptyColor else NonEmptyColor)
    }
}