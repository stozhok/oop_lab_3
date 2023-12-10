package org.example.student.battleshipgame

import uk.ac.bournemouth.ap.battleshiplib.Ship
import java.lang.IllegalArgumentException

class StudentShip(override val top: Int, override val left: Int, override val bottom: Int, override val right: Int) : Ship {
    init {
        //Make sure to check that the arguments are valid: left<=right, top<=bottom and the * ship is only 1 wide
        if (right < left || bottom < top || (bottom != top && right != left) || left < 0 || top < 0) {
            throw IllegalArgumentException("Ship dimensions not possible")
        }
    }

    fun overlaps(other: StudentShip): Boolean {
        return (other.right >= left && other.left <= right &&
                other.bottom >= top && other.top <= bottom)
    }

    fun isCoordinateInShip(column: Int, row: Int): Boolean {
        return column in columnIndices && row in rowIndices
    }
}