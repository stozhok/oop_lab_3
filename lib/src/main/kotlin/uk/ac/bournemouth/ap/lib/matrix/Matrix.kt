package uk.ac.bournemouth.ap.lib.matrix

import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate


/**
 * A matrix type (based upon [SparseMatrix]) but it has values at all coordinates.
 */
interface Matrix<out T> : MatrixCommon<T>, SparseMatrix<T> {

    override val indices: Iterable<Coordinate> get() = super<MatrixCommon>.indices

    override fun copyOf(): Matrix<T>

    override val validator get() = super<MatrixCommon>.validator
}

/**
 * A 2-dimensional storage type/matrix that does not require values in all cells. This is a
 * read-only type. The writable version is [MutableSparseMatrix]. The minimum coordinate is always
 * 0. This implements [Iterable] to allow you to get all values (this relies on [isValid]).
 */
interface SparseMatrix<out T> : SparseMatrixCommon<T> {
    /**
     * Operator to get the values out of the matrix.
     */
    override operator fun get(x: Int, y: Int): T

    override val indices: Iterable<Coordinate> get() = SparseMatrixIndices(this)

    override fun copyOf(): SparseMatrix<T>

    override fun asSequence(): Sequence<T>
}

/**
 * A mutable version of [SparseMatrix] that adds a setter ([set]) to allow for changing the values
 * in the matrix.
 */
interface MutableSparseMatrix<T> : SparseMatrix<T> {
    /**
     * Operator/function to set the value at the given coordinate.
     */
    operator fun set(x: Int, y: Int, value: T)
    override fun copyOf(): MutableSparseMatrix<T>
}

/**
 * An extension to Matrix that is mutable. This is effectively a 2D array.
 */
interface MutableMatrix<T> : MutableSparseMatrix<T>,
    Matrix<T> {
    override fun copyOf(): MutableMatrix<T>
}

inline fun <T, R> Matrix<T>.map(transform: (T) -> R): Matrix<R> {
    return MutableMatrix(width, height) { x, y -> transform(get(x, y)) }
}

inline fun <T, R> SparseMatrix<T>.map(transform: (T) -> R): SparseMatrix<R> = when (this) {
    is Matrix -> this.map(transform) //delegate to more specific function

    else -> {
        val validate: (Int, Int) -> Boolean = when (this) {
            is ArrayMutableSparseMatrix -> validator
            else -> { x, y -> isValid(x, y) }
        }

        MutableSparseMatrix(
            maxWidth,
            maxHeight,
            init = { x, y -> transform(get(x, y)) },
            validate = validate
        )
    }
}

inline fun <T, R> MatrixCommon<T>.map(transform: (T) -> R): Matrix<R> {
    return MutableMatrix(width, height) { x, y -> transform(get(x, y)) }
}

inline fun <T, R> SparseMatrixCommon<T>.map(transform: (T) -> R): SparseMatrix<R> = when {
    // When we know that our validator is for an effectively non-sparse matrix, create a regular matrix
    validator == MatrixCommon.VALIDATOR ||
            this is MatrixCommon ->
        MutableMatrix(maxWidth, maxHeight) { x, y -> transform(get(x, y)) }


    else -> {
        val validate: (Int, Int) -> Boolean = when (this) {
            is ArrayMutableSparseIntMatrix -> validator
            is ArrayMutableSparseMatrix<*> -> validator
            else -> { x, y -> isValid(x, y) }
        }

        MutableSparseMatrix(
            maxWidth,
            maxHeight,
            init = { x, y -> transform(get(x, y)) },
            validate = validate
        )
    }
}
