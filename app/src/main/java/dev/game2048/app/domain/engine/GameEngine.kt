package dev.game2048.app.domain.engine

import dev.game2048.app.domain.model.Direction
import dev.game2048.app.domain.model.Tile
import dev.game2048.app.utils.GameConstants
import kotlin.collections.plusAssign
import kotlin.random.Random

class GameEngine(private val size: Int) {

    private var grid: Array<Array<Tile?>> = Array(size) { Array(size) { null } }
    private val emptyCells = mutableListOf<Pair<Int, Int>>()

    var hasWon: Boolean = false
        private set
    var winTarget: Int = GameConstants.WIN_VALUE
        private set
    var score: Int = 0
        private set

    var hasMerged: Boolean = false
    val board: List<List<Tile?>> get() = grid.map { it.toList() }

    fun startGame() {
        hasWon = false
        winTarget = GameConstants.WIN_VALUE
        score = 0
        grid.forEach { row -> row.fill(null) }

        refreshEmptyCells()
        repeat(GameConstants.INITIAL_TILES) { spawnRandomTile() }
    }

    fun restore(boardSnapshot: List<List<Tile?>>, previousScore: Int, previousWinTarget: Int) {
        for (r in 0 until size) {
            for (c in 0 until size) {
                grid[r][c] = boardSnapshot[r][c]
            }
        }
        hasWon = false
        winTarget = previousWinTarget
        score = previousScore

        refreshEmptyCells()
    }

    fun isGameOver(): Boolean {
        if (refreshEmptyCells()) return false
        return computeGrid(Direction.LEFT).contentDeepEquals(grid) &&
            computeGrid(Direction.UP).contentDeepEquals(grid)
    }

    fun spawnRandomTile() {
        if (emptyCells.isEmpty()) return

        val (row, col) = emptyCells.removeAt(Random.nextInt(emptyCells.size))
        grid[row][col] = Tile(value = if (Random.nextFloat() < GameConstants.CHANCE_OF_TWO) 2 else 4)
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

    fun doubleWinTarget() {
        winTarget *= 2
        hasWon = false
    }

    private fun computeGrid(direction: Direction): Array<Array<Tile?>> {
        val reverse = direction == Direction.RIGHT || direction == Direction.DOWN
        val vertical = direction == Direction.UP || direction == Direction.DOWN

        val source = if (vertical) transpose(grid) else grid
        val moved = source.map { row -> slide(row, reverse) }.toTypedArray()

        return if (vertical) transpose(moved) else moved
    }

    private fun slide(row: Array<Tile?>, reverse: Boolean): Array<Tile?> {
        val filtered = row.filterNotNull().let { if (reverse) it.reversed() else it }
        val merged = mergeRow(filtered)
        val padded = merged + List(size - merged.size) { null }
        return (if (reverse) padded.reversed() else padded).toTypedArray()
    }

    private fun refreshEmptyCells(): Boolean {
        emptyCells.clear()

        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == null) emptyCells.add(r to c)
            }
        }

        return emptyCells.isNotEmpty()
    }

    private fun mergeRow(row: List<Tile>): List<Tile> {
        val result = mutableListOf<Tile>()
        var i = 0

        while (i < row.size) {
            if (i < row.lastIndex && row[i].value == row[i + 1].value) {
                hasMerged = true
                val merged = row[i].value * 2
                result.add(Tile(id = row[i].id, value = merged))
                score += merged
                if (merged == winTarget) hasWon = true
                i += 2
            } else {
                result.add(row[i])
                i++
            }
        }
        return result
    }

    private fun transpose(g: Array<Array<Tile?>>): Array<Array<Tile?>> =
        Array(size) { i -> Array(size) { j -> g[j][i] } }
}
