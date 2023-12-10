package org.example.student.battleshipgame

import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshiplib.GuessResult
import kotlin.random.Random


class StudentOpponentAi {

    fun shootAtPlayer(grid: StudentBattleshipGrid): GuessResult {
        val random = Random

        var columnRandom: Int
        var rowRandom: Int

        //generates new random coordinates if they have already been used.
        do {
            columnRandom = random.nextInt(0, grid.columns)
            rowRandom = random.nextInt(0, grid.rows)

        } while (grid[columnRandom, rowRandom] != GuessCell.UNSET)

        return grid.shootAt(columnRandom, rowRandom)
    }

}