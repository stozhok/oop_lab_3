package uk.ac.bournemouth.ap.battleshiplib

import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate


interface BattleshipGrid {
    val columns: Int get() = opponent.columns
    val rows: Int get() = opponent.rows

    val opponent: BattleshipOpponent

    @Deprecated("Not needed", ReplaceWith("opponent.ships"))
    val ships: List<Ship> get() = opponent.ships

    /**
     * An array determining whether the ship with the given index was sunk. This can be used for
     * various purposes, including to determine whether the game has been won.
     *
     * @return An array with the status of sinking of each ship
     */
    val shipsSunk: BooleanArray

    val isFinished: Boolean get() = shipsSunk.all { it }

    operator fun get(column: Int, row: Int): GuessCell

    fun shootAt(column: Int, row: Int): GuessResult

    /**
     * Register a listener for game changes
     *
     * @param listener The listener to register.
     */
    fun addOnGridChangeListener(listener: BattleshipGridListener)

    /**
     * Unregister a listener so that it no longer receives notifications of game changes
     *
     * @param listener The listener to unregister.
     */
    fun removeOnGridChangeListener(listener: BattleshipGridListener)

    fun interface BattleshipGridListener {
        /**
         * When the game is changed this method is called.
         *
         * @param grid   The game object that is the source of the event.
         * @param column The column changed
         * @param row    The row changed
         */
        fun onGridChanged(grid: BattleshipGrid, column: Int, row: Int)
    }

    companion object {

        @JvmStatic
        val DEFAULT_SHIP_SIZES = intArrayOf(
            5, // Carrier
            4, // Battleship"
            3, // Cruiser"
            3, // Submarine"
            2  // Destroyer
        )

        const val DEFAULT_COLUMNS = 10

        const val DEFAULT_ROWS = 10
    }
}


/** Shortcut for using with coordinates. */
inline fun BattleshipGrid.shootAt(coordinate: Coordinate): GuessResult =
    shootAt(coordinate.x, coordinate.y)

inline operator fun BattleshipGrid.get(coordinate: Coordinate) = get(coordinate.x, coordinate.y)

inline val BattleshipGrid.coordinates: Sequence<Coordinate>
    get() = (0 until rows)
        .asSequence()
        .flatMap { y ->
            (0 until columns).asSequence().map { x -> Coordinate(x, y) }
        }