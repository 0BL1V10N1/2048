package dev.game2048.app.data.sources

import dev.game2048.app.data.models.Direction
import kotlin.random.Random

class GameEngine(private val size: Int = DEFAULT_SIZE) {

    private var grid: Array<IntArray> = Array(size) { IntArray(size) }
    private val emptyCells = mutableListOf<Pair<Int, Int>>()
    var hasWon: Boolean = false
        private set

    val board: List<List<Int>> get() = grid.map { it.toList() }

    fun startGame() {
        hasWon = false
        grid.forEach { row -> row.fill(0) }

        refreshEmptyCells()
        repeat(INITIAL_TILES) { spawnRandomTile() }
    }

    fun isGameOver(): Boolean {
        if (refreshEmptyCells()) return false
        return computeGrid(Direction.LEFT).contentDeepEquals(grid) &&
            computeGrid(Direction.UP).contentDeepEquals(grid)
    }

    fun spawnRandomTile() {
        if (emptyCells.isEmpty()) return

        val (row, col) = emptyCells.removeAt(Random.nextInt(emptyCells.size))
        grid[row][col] = if (Random.nextFloat() < CHANCE_OF_TWO) 2 else 4
    }

    fun move(direction: Direction): Boolean {
        val newGrid = computeGrid(direction)
        val hasChanged = !newGrid.contentDeepEquals(grid)

        if (hasChanged) {
            grid = newGrid
            refreshEmptyCells()
        }

        return hasChanged
    }

    private fun computeGrid(direction: Direction): Array<IntArray> {
        val reverse = direction == Direction.RIGHT || direction == Direction.DOWN
        val vertical = direction == Direction.UP || direction == Direction.DOWN

        val source = if (vertical) transpose(grid) else grid
        val moved = source.map { row -> slide(row, reverse) }.toTypedArray()

        return if (vertical) transpose(moved) else moved
    }

    private fun slide(row: IntArray, reverse: Boolean): IntArray {
        val filtered = row.filter { it != 0 }.let { if (reverse) it.reversed() else it }
        val merged = mergeRow(filtered)
        val padded = merged + List(size - merged.size) { 0 }

        return (if (reverse) padded.reversed() else padded).toIntArray()
    }

    private fun refreshEmptyCells(): Boolean {
        emptyCells.clear()

        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == 0) emptyCells.add(r to c)
            }
        }

        return emptyCells.isNotEmpty()
    }

    private fun mergeRow(row: List<Int>): List<Int> {
        val result = mutableListOf<Int>()
        var i = 0

        while (i < row.size) {
            if (i < row.lastIndex && row[i] == row[i + 1]) {
                val merged = row[i] * 2
                result.add(merged)
                if (merged == WIN_VALUE) hasWon = true
                i += 2
            } else {
                result.add(row[i])
                i++
            }
        }

        return result
    }

    private fun transpose(g: Array<IntArray>): Array<IntArray> = Array(size) { i -> IntArray(size) { j -> g[j][i] } }

    private companion object {
        const val DEFAULT_SIZE = 4
        const val INITIAL_TILES = 2
        const val CHANCE_OF_TWO = 0.9f
        const val WIN_VALUE = 2048
    }
}
