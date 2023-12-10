package org.example.student.battleshipgame

import uk.ac.bournemouth.ap.battleshiplib.BattleshipGame
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid

//implements BattleshipGame that contains the grids of the game
open class StudentBattleshipGame(override val grids: List<BattleshipGrid>) : BattleshipGame