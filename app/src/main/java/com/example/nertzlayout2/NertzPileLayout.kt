package com.example.nertzlayout2

import android.content.Context
import android.view.ViewGroup
import androidx.cardview.widget.CardView

class NertzPileLayout(parent: ViewGroup, x: Int, y: Int, width: Int, height: Int):
    PileLayout(parent, x, y, width, height, WinButton(parent.context))
{}