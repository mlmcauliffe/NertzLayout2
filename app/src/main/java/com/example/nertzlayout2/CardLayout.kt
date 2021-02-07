package com.example.nertzlayout2

import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView

class CardLayout(parent: ViewGroup, card: CardMgr, var pile: PileLayout, var posInPile: Int):
        CardView(parent.context) {

    companion object {
        val SuitColors = arrayOf("#000000", "#F44336", "#F44336", "#000000").map { Color.parseColor( it ) }
        val radiusDivisor = 15f
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.nertz_card, this)
        with (view.findViewById(R.id.title_value) as TextView) {
            setTextColor(SuitColors[card.suit])
            text = context.getString(R.string.card_value, CardValueStrings[card.value])
            textSize = 22f
        }
        with (view.findViewById(R.id.title_suit) as TextView) {
            setTextColor(SuitColors[card.suit])
            text = context.getString(R.string.card_suit, SuitStrings[card.suit])
            textSize = 18f
        }
        with (view.findViewById(R.id.body_suit) as TextView) {
            setTextColor(SuitColors[card.suit])
            text = context.getString(R.string.card_suit, SuitStrings[card.suit])
            textSize = 38f
        }

        setContentPadding(8, 0, 8, 10)
        parent.addView(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = w / CardLayout.radiusDivisor
    }
}
