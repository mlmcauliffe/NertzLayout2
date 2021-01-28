package com.example.nertzlayout2

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView

/*
@SuppressLint("ViewConstructor")
class NertzCardViewX(parent: ViewGroup, card: NertzCard, var pile: PileLayout, var posInPile: Int = 0)
    : CardView(parent.context) {

    companion object {
        val colors = arrayOf(
                0xFFF44336.toInt(), 0xFFFFEB3B.toInt(), 0xFF3F51B5.toInt(), 0xFF9C27B0.toInt())
        val suitColors = Suit.values().zip(colors).toMap()
        val radiusDivisor = 15f
    }

    init {
        val textView = TextView(this.context)
        textView.setTextColor(Color.parseColor(SuitColors[card.suit]))
        textView.text = "${CardValueStrings[card.value]} ${SuitStrings[card.suit]}"
        textView.textSize = 20f
        this.addView(textView)
        parent.addView(this)
        //setCardBackgroundColor(suitColors[card.suit]!!)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = w / radiusDivisor
    }

    var animationStartX = 0
    var animationStartY = 0
}

 */


class NertzCardView(parent: ViewGroup, card: NertzCard, var pile: PileLayout, var posInPile: Int):
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
        radius = w / NertzCardView.radiusDivisor
    }

    var animationStartX = 0
    var animationStartY = 0
}
