package org.example.student.battleshipgame

import uk.ac.bournemouth.ap.battleshiplib.*
import uk.ac.bournemouth.ap.battleshiplib.test.BattleshipTest
import uk.ac.bournemouth.ap.lib.matrix.BooleanMatrix
import uk.ac.bournemouth.ap.lib.matrix.MutableBooleanMatrix
import kotlin.random.Random

class StudentBattleshipTest : BattleshipTest() {
    override fun createOpponent(
            columns: Int,
            rows: Int,
            ships: List<Ship>
    ): StudentBattleshipOpponent {
        val studentShips = ships.map { ship ->
            StudentShip(top = ship.top, left = ship.left, bottom = ship.bottom, right = ship.right)
        }
        return StudentBattleshipOpponent(columns, rows, studentShips)
    }

    override fun createOpponent(
            columns: Int,
            rows: Int,
            shipSizes: IntArray,
            random: Random
    ): StudentBattleshipOpponent {
        return StudentBattleshipOpponent(columns, rows, shipSizes, random)
    }

    override fun createGrid(
            grid: BooleanMatrix,
            opponent: BattleshipOpponent
    ): StudentBattleshipGrid {
        val studentOpponent =
                opponent as? StudentBattleshipOpponent
                        ?: createOpponent(opponent.columns, opponent.rows, opponent.ships)

        return StudentBattleshipGrid(studentOpponent)
    }

    override fun createGame(grids: List<BattleshipGrid>): BattleshipGame {
        val studentGrids = grids.map { grid ->
            grid as? StudentBattleshipGrid
                    ?: createGrid(MutableBooleanMatrix(grid.columns, grid.rows), grid.opponent)
        }
        return StudentBattleshipGame(studentGrids)
    }
}
