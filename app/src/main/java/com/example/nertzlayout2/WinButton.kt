package com.example.nertzlayout2

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import androidx.cardview.widget.CardView

class WinButton(context: Context): CardView(context) {
    init {
        setCardBackgroundColor(Color.CYAN)
        setOnTouchListener(object : OnTouchListener {
            override fun onTouch(p0: View, event: MotionEvent): Boolean {
                if (event.action != MotionEvent.ACTION_DOWN) return false
                val builder = AlertDialog.Builder(context)
                builder.setMessage("You Win!")
                builder.show()
                return true
            }
        })
    }
}