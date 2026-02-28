package dev.game2048.app.data

import kotlin.random.Random

class GameEngine(private val size: Int = DEFAULT_SIZE) {

    private val grid: Array<IntArray> = Array(size) { IntArray(size) }

    /** Returns an immutable snapshot of the current board. */
    fun board(): List<List<Int>> = grid.map { it.toList() }

    fun startGame() {
        grid.forEach { row -> row.fill(0) }
        repeat(INITIAL_TILES) { spawnRandomTile() }
    }

    fun spawnRandomTile() {
        val emptyCells = buildList {
            for (row in 0 until this@GameEngine.size) {
                for (col in 0 until this@GameEngine.size) {
                    if (grid[row][col] == 0) add(row to col)
                }
            }
        }

        if (emptyCells.isEmpty()) return

        val (row, col) = emptyCells.random()
        grid[row][col] = if (Random.nextFloat() < CHANCE_OF_TWO) 2 else 4
    }

    fun move(direction: Direction) {
        // TODO: fusion
        spawnRandomTile()
    }

    private companion object {
        const val DEFAULT_SIZE = 4
        const val INITIAL_TILES = 2
        const val CHANCE_OF_TWO = 0.9f
    }
}
