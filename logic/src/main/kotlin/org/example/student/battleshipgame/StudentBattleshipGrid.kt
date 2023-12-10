package org.example.student.battleshipgame

import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid.BattleshipGridListener
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshiplib.GuessResult
import uk.ac.bournemouth.ap.battleshiplib.forEachIndex
import uk.ac.bournemouth.ap.lib.matrix.Matrix
import uk.ac.bournemouth.ap.lib.matrix.MutableMatrix
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/**
 * This grid class describes the state of current guesses. It records which ships were sunk, where
 * shots were placed (with what results). It also records the [opponent](StudentBattleshipOpponent)
 *
 * @constructor Constructor that represents the actual state, this is needed when saving/loading
 *   the state.
 * @param guesses   The information on the ships in the grid.
 * @param opponent The actual opponent information.
 */
open class StudentBattleshipGrid protected constructor(
        guesses: Matrix<GuessCell>,
        override val opponent: StudentBattleshipOpponent,
) : BattleshipGrid {

    /**
     * Helper constructor for a fresh new game
     */
    constructor(opponent: StudentBattleshipOpponent = StudentBattleshipOpponent()) : this(
            MutableMatrix(
                    opponent.columns,
                    opponent.rows
            ) { _, _ -> GuessCell.UNSET }, opponent
    )

    /**
     * A list of listeners that should be informed if the game state changes.
     */
    private val onGridChangeListeners = mutableListOf<BattleshipGridListener>()

    /**
     * An array determining whether the ship with the given index was sunk. This can be used for
     * various purposes, including to determine whether the game has been won.
     *
     * @return An array with the status of sinking of each ship
     */
    override val shipsSunk: BooleanArray by lazy { BooleanArray(opponent.ships.size) }
    // This property is lazy to resolve issues with order of initialization.

    /**
     * A matrix with all guesses made in the game
     */
    private val guesses: MutableMatrix<GuessCell> = MutableMatrix(guesses)

    /**
     * Helper property to get the width of the game.
     */
    override val columns: Int get() = opponent.columns

    /**
     * Helper property to get the height of the game.
     */
    override val rows: Int get() = opponent.rows

    /*
     * Infrastructure to allow listening to game change events (and update the display
     * correspondingly)
     */

    /**
     * Register a listener for game changes
     *
     * @param listener The listener to register.
     */
    override fun addOnGridChangeListener(listener: BattleshipGridListener) {
        if (!onGridChangeListeners.contains(listener)) onGridChangeListeners.add(listener)
    }

    /**
     * Unregister a listener so that it no longer receives notifications of game changes
     *
     * @param listener The listener to unregister.
     */
    override fun removeOnGridChangeListener(listener: BattleshipGridListener) {
        onGridChangeListeners.remove(listener)
    }

    /**
     * Send a game change event to all registered listeners.
     *
     * @param column The column changed
     * @param row    The row changed
     */
    private fun fireOnGridChangeEvent(column: Int, row: Int) {
        for (listener in onGridChangeListeners) {
            listener.onGridChanged(this, column, row)
        }
    }

    /**
     * The get operator allows retrieving the guesses at a location. You probably want to just look
     * the value up from a property you create (of type `MutableMatrix<GuessCell>`)
     */
    override operator fun get(column: Int, row: Int): GuessCell = guesses[column, row]

    /**
     * This method is core to the game as it implements the actual game play (after initial setup).
     */
    override fun shootAt(column: Int, row: Int): GuessResult {
        //Checks that the coordinates are in range
        if (column < 0 || column >= columns ||
                row < 0 || row >= rows) {
            throw IllegalArgumentException("coordinates are not in range")
        }
        //Checks that the coordinate has not been tried already for this game
        if (guesses[column, row] != GuessCell.UNSET) {
            throw IllegalStateException("coordinate already guessed")
        }

        //Updates the grid state, remembering that if a ship is sunk, all its cells should be sunk"
        @Suppress("MoveVariableDeclarationIntoWhen")
        val shipInfo = opponent.shipAt(column, row)
        val guessResult = when (shipInfo) {

            null -> {
                guesses[column, row] = GuessCell.MISS
                GuessResult.MISS
            }

            else -> {
                guesses[column, row] = GuessCell.HIT(shipInfo.index)

                var isSunk = true
                shipInfo.ship.forEachIndex { x, y ->
                    if (guesses[x, y] !is GuessCell.HIT) {
                        isSunk = false
                    }
                }

                if (isSunk) {
                    shipInfo.ship.forEachIndex { x, y ->
                        guesses[x, y] = GuessCell.SUNK(shipInfo.index)
                    }
                    shipsSunk[shipInfo.index] = true
                    GuessResult.SUNK(shipInfo.index)
                } else {
                    GuessResult.HIT(shipInfo.index)
                }


            }
        }
        fireOnGridChangeEvent(column, row)

        return guessResult
    }

}
