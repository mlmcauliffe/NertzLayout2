package com.example.nertzlayout2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import com.example.nertzlayout2.databinding.ActivityMainBinding

class DpToPx(val dm: DisplayMetrics) {
    operator fun invoke(dp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), dm)
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val tableTop = binding.tableTop
        val undo = binding.undo
        val dm: DisplayMetrics = resources.displayMetrics

        val game = Game(4)

        val layoutParams = genParams(game, dm)
        val layout = GameBoardLayout(
            dm.widthPixels,
            dm.heightPixels,
            layoutParams
        )
        val gameBoard = GameLayout(tableTop, layout)
        val gc = GameController(game, gameBoard)
        val player = UIPlayer(gc, undo)
        setContentView(binding.root)
    }

    fun genParams(game: Game, dm: DisplayMetrics): GameBoardLayoutParams {
        val dpToPx = DpToPx(dm)
        return GameBoardLayoutParams(
            dpToPx(10),
            dpToPx(10),
            dpToPx(80),
            0.7f,
            game.cascadePiles.size,
            0.25f,
            1.0f,
            2f,
            dpToPx(20))
    }
}