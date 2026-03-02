package dev.game2048.app.data

import android.util.Log
import kotlin.random.Random

class GameEngine(private val size: Int = DEFAULT_SIZE) {

    private var grid: Array<IntArray> = Array(size) { IntArray(size) }
    private val emptyCells = mutableListOf<Pair<Int, Int>>()

    /** Returns a mutable snapshot of the current board. */
    fun board(): List<List<Int>> = grid.map { it.toList() }

    fun startGame() {
        grid.forEach { row -> row.fill(0) }

        emptyCells.clear()
        for (r in 0 until size) {
            for (c in 0 until size) {
                emptyCells.add(r to c)
            }
        }

        repeat(INITIAL_TILES) { spawnRandomTile() }
    }

    fun spawnRandomTile() {
        if (emptyCells.isEmpty()) return

        val index = Random.nextInt(emptyCells.size)
        val (row, col) = emptyCells.removeAt(index)

        grid[row][col] = if (Random.nextFloat() < CHANCE_OF_TWO) 2 else 4
    }

    private fun updateEmptyCells() {
        emptyCells.clear()
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == 0) emptyCells.add(r to c)
            }
        }
    }

    fun move(direction: Direction): Boolean {
        val newGrid = when (direction) {
            Direction.UP -> moveUp()
            Direction.DOWN -> moveDown()
            Direction.LEFT -> moveLeft()
            Direction.RIGHT -> moveRight()
        }

        // if the grid has changed, update it and spawn a new tile
        val hasChanged: Boolean = !newGrid.contentDeepEquals(grid)
        if (hasChanged) {
            grid = newGrid
            updateEmptyCells()
            spawnRandomTile()
        }

        return hasChanged
    }

    private fun fillZ(row: List<Int>) = (row + List(size - row.size) { 0 })

    private fun mergeRow(row: List<Int>): List<Int> {
        val result = mutableListOf<Int>()
        var i = 0

        while (i < row.size) {
            if (i < row.lastIndex && row[i] == row[i + 1]) {
                result.add(row[i] * 2)
                i += 2
            } else {
                result.add(row[i])
                i++
            }
        }

        return result
    }

    // Allows us to reuse the merge method for up and down transformations
    // The columns become rows and are easy to manipulate
    private fun transpose(grid: Array<IntArray>): Array<IntArray> = Array(size) { i ->
        IntArray(size) { j -> grid[j][i] }
    }

    fun moveLeft(): Array<IntArray> = grid.map { row ->
        fillZ(
            mergeRow(
                row.filter { it != 0 }
            )
        ).toIntArray()
    }.toTypedArray()

    fun moveRight(): Array<IntArray> = grid.map { row ->
        fillZ(
            mergeRow(row.filter { it != 0 }.reversed())
        ).reversed().toIntArray()
    }.toTypedArray()

    fun moveUp(): Array<IntArray> {
        val transposed = transpose(grid)
        val moved = transposed.map { row ->
            fillZ(mergeRow(row.filter { it != 0 })).toIntArray()
        }.toTypedArray()
        return transpose(moved)
    }
    fun moveDown(): Array<IntArray> {
        val transposed = transpose(grid)
        val moved = transposed.map { row ->
            fillZ(mergeRow(row.filter { it != 0 }.reversed())).reversed().toIntArray()
        }.toTypedArray()
        return transpose(moved)
    }

    private fun logBoard(grid: Array<IntArray>) {
        Log.d(
            "swipe",
            grid.joinToString
                (", ", "[", "]")
                { it.joinToString(", ", "[", "]") }
        )
    }

    private companion object {
        const val DEFAULT_SIZE = 4
        const val INITIAL_TILES = 2
        const val CHANCE_OF_TWO = 0.9f
    }
}
