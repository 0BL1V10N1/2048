package dev.game2048.app

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.game2048.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //  used later to do the calculations
    private lateinit var board: Array<Array<Int>>

    // used for the tiles displaying
    private lateinit var tiles: Array<Array<TextView>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createGrid(4)
    }

    private fun createGrid(dims: Int) {
        tiles =
            Array(dims) { row ->
                Array(dims) { col ->
                    val tile = createTile(row, col)
                    binding.grid.addView(tile)
                    tile
                }
            }
    }

    private fun createTile(row: Int, col: Int): TextView {
        val tile = TextView(this)

        val params =
            GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                rowSpec = GridLayout.spec(row, 1f)
                columnSpec = GridLayout.spec(col, 1f)
                setMargins(8, 8, 8, 8)
            }

        tile.layoutParams = params
        tile.gravity = Gravity.CENTER
        tile.textSize = 24f
        tile.setTypeface(null, Typeface.BOLD)
        tile.setBackgroundResource(R.drawable.tile_background)

        return tile
    }
}
