package uk.ac.bournemouth.ap.battleshiplib

interface BattleshipGame {
    val columns: Int get() = grids.first().columns
    val rows: Int get() = grids.first().rows

    val grids: List<BattleshipGrid>
}
