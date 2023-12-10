package uk.ac.bournemouth.ap.lib.matrix

import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate

/**
 * Functionality that is shared among all Matrix implementations
 * (for primitives and object ones)
 */
interface SparseMatrixCommon<out T>: Iterable<T> {
    /** The maximum x coordinate that is valid which can be stored. */
    val maxWidth: Int

    /** The maximum x coordinate that is valid */
    val maxHeight: Int

    val indices: Iterable<Coordinate>

    /**
     * This function can be used to determine whether the given coordinates are valid. Returns
     * true if valid. This function works on any value for the coordinates and should return `false`
     * for all values out of range (`x<0 || x>=[maxWidth]`), (`y<0 || y>=[maxHeight]`).
     */
    fun isValid(x: Int, y: Int): Boolean

    /**
     * Whatever the actual type, allow them to be read to read any value. Implementations are
     * expected to use more precise return types.
     */
    operator fun get(x: Int, y: Int): T

    /**
     * For copying allow retrieving the/a validator function
     */
    val validator: (Int, Int) -> Boolean

    /**
     * Creates a copy of the matrix of an appropriate type with the same content.
     */
    fun copyOf(): SparseMatrixCommon<T>

    override fun iterator(): Iterator<T> = object : Iterator<T> {

        private val indexIterator = indices.iterator()

        override fun hasNext(): Boolean {
            return indexIterator.hasNext()
        }

        override fun next(): T {
            val idx = indexIterator.next()
            return get(idx.x, idx.y)
        }

    }


    fun asSequence(): Sequence<Any?>

}

inline fun SparseMatrixCommon<*>.forEachIndex(action: (Int, Int) -> Unit) {
    for (x in 0 until maxWidth) {
        for (y in 0 until maxHeight) {
            if (isValid(x, y)) {
                action(x, y)
            }
        }
    }
}

inline fun MatrixCommon<*>.forEachIndex(action: (Int, Int) -> Unit) {
    for (x in 0 until maxWidth) {
        for (y in 0 until maxHeight) {
            action(x, y)
        }
    }
}

inline fun <T> SparseMatrix<T>.forEach(action: (T) -> Unit) {
    for (x in 0 until maxWidth) {
        for (y in 0 until maxHeight) {
            if (isValid(x, y)) {
                action(get(x, y))
            }
        }
    }
}

inline fun <T> Matrix<T>.forEach(action: (T) -> Unit) {
    for (x in 0 until maxWidth) {
        for (y in 0 until maxHeight) {
            action(get(x, y))
        }
    }
}

fun SparseMatrixCommon<*>.toStringImpl(): String {
    val strings: SparseMatrix<String> = map { it.toString() }
    val maxColWidth = strings.asSequence().map { it.length }.maxOrNull() ?: 0
    val capacity = maxWidth * maxColWidth + 2
    return buildString(capacity) {
        for (row in 0 until maxWidth) {
            append(if (row == 0) '(' else ' ')

            for (col in 0 until maxHeight) {
                val cellString = if (isValid(col, row)) strings[col, row] else ""

                for (i in 1..(maxColWidth - cellString.length)) {
                    append(' ')
                }
                append(cellString)
            }
        }
        append(')')
    }

}

/**
 * Helper function for implementing matrices that throws an exception if the
 * coordinates are out of range.
 */
internal fun SparseMatrixCommon<*>.validate(x: Int, y: Int) {
    if (!isValid(x, y)) {
        throw IndexOutOfBoundsException("($x,$y) out of range: ($maxWidth, $maxHeight)")
    }
}

interface MatrixCommon<out T> : SparseMatrixCommon<T> {

    /** The width of the matrix. This is effectively the same as [maxWidth]. */
    val width: Int get() = maxWidth

    /** The height of the matrix. This is effectively the same as [maxWidth]. */
    val height: Int get() = maxHeight

    override val indices: Iterable<Coordinate> get() = MatrixIndices(this)

    /**
     * The indices of all columns in the matrix
     */
    val columnIndices: IntRange get() = 0 until width

    /**
     * The indices of all rows in the matrix
     */
    val rowIndices: IntRange get() = 0 until height

    /**
     * This implementation will just check that the coordinates are in range. There should be no
     * reason to no use this default implementation.
     */
    override fun isValid(x: Int, y: Int): Boolean {
        return x in 0 until width && y in 0 until height
    }

    override fun copyOf(): MatrixCommon<T>

    override val validator: (Int, Int) -> Boolean
        get() = VALIDATOR

    companion object {
        val VALIDATOR: (Int, Int) -> Boolean = { _, _ -> true }
    }
}


internal class SparseMatrixIndices(private val matrix: SparseMatrixCommon<*>) :
    Iterable<Coordinate> {

    override fun iterator(): Iterator<Coordinate> = IteratorImpl(matrix)

    private class IteratorImpl(private val matrix: SparseMatrixCommon<*>) :
        Iterator<Coordinate> {
        private var nextPoint = -1
        private val maxPoint get() = matrix.maxWidth * matrix.maxHeight
        private val width get() = matrix.maxWidth

        init {
            moveToNext()
        }

        private fun moveToNext() {
            do {
                nextPoint++
            } while (nextPoint < maxPoint && !isValidPoint(nextPoint))
        }

        private fun isValidPoint(point: Int): Boolean {
            return matrix.isValid(point % matrix.maxWidth, point / matrix.maxWidth)
        }

        override fun hasNext(): Boolean {
            return nextPoint < maxPoint
        }

        override fun next(): Coordinate {
            val point = nextPoint
            moveToNext()
            return Coordinate(point % width, point / width)
        }
    }
}

internal class MatrixIndices(private val matrix: MatrixCommon<*>) : Iterable<Coordinate> {
    override fun iterator(): Iterator<Coordinate> = IteratorImpl(matrix)

    private class IteratorImpl(matrix: MatrixCommon<*>) : Iterator<Coordinate> {
        private var nextPoint = 0
        private val maxPoint = matrix.width * matrix.height
        private val width = matrix.width

        override fun hasNext(): Boolean {
            return nextPoint < maxPoint
        }

        override fun next(): Coordinate {
            val point = nextPoint
            ++nextPoint
            return Coordinate(point % width, point / width)
        }
    }

}