package com.example.nertzlayout2

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView

@SuppressLint("ViewConstructor")
class NertzCardView(parent: ViewGroup, card: NertzCard, var pile: PileLayout, var posInPile: Int = 0)
    : View(parent.context) {

    companion object {
        val colors = arrayOf(
                0xFFF44336.toInt(), 0xFFFFEB3B.toInt(), 0xFF3F51B5.toInt(), 0xFF9C27B0.toInt())
        val suitColors = Suit.values().zip(colors).toMap()
    }

    init {
        parent.addView(this)
        setBackgroundColor(suitColors[card.suit]!!)
    }

    var animationStartX = 0
    var animationStartY = 0
}