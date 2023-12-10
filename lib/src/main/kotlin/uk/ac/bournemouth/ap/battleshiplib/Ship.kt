package uk.ac.bournemouth.ap.battleshiplib

import uk.ac.bournemouth.ap.lib.matrix.IntMatrix
import uk.ac.bournemouth.ap.lib.matrix.Matrix
import uk.ac.bournemouth.ap.lib.matrix.MutableMatrix

interface Ship {
    val top: Int
    val left: Int
    val bottom: Int
    val right: Int

    val columnIndices: IntRange get() = left..right
    val rowIndices: IntRange get() = top..bottom

    val width: Int get() = right - left + 1
    val height: Int get() = bottom - top + 1
    val size: Int get() = width*height
}

inline fun Ship.forEachIndex(action: (Int, Int) -> Unit) {
    for (x in columnIndices) {
        for (y in rowIndices) {
            action(x, y)
        }
    }
}

inline fun Ship.mapIndices(action: (Int, Int) -> Int): IntMatrix {
    return IntMatrix(width, height) { mx, my ->
        action(mx+left, my+top)
    }
}

inline fun <T> Ship.mapIndices(action: (Int, Int) -> T): Matrix<T> {
    return MutableMatrix(width, height) { mx, my ->
        action(mx+left, my+top)
    }
}
