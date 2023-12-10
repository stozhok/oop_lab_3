package uk.ac.bournemouth.ap.lib.matrix

import java.lang.IndexOutOfBoundsException

inline fun MutableBooleanMatrix(width: Int, height: Int, init: (Int, Int)->Boolean): MutableBooleanMatrix {
    return MutableBooleanMatrix(width, height).also { m ->
        for (x in 0 until width) {
            for (y in 0 until height) {
                m[x, y] = init(x, y)
            }
        }
    }
}

interface BooleanMatrix: MatrixCommon<Boolean> {
    override val width: Int
    override val height: Int
}

class MutableBooleanMatrix private constructor(override val width: Int, private val data: BooleanArray):
    BooleanMatrix, Iterable<Boolean> {

    override val maxWidth: Int get() = width
    override val maxHeight: Int get() = height

    override val height: Int get() = data.size/width

    constructor(width: Int, height: Int): this (width, BooleanArray(width*height))
    constructor(source: MutableBooleanMatrix): this(source.width, source.data.copyOf())

    override fun copyOf(): MutableBooleanMatrix =
        MutableBooleanMatrix(width, data.copyOf())

    operator fun set(x:Int, y:Int, value: Boolean) {
        if (x !in 0 until width ||
            y !in 0 until height) throw IndexOutOfBoundsException("($x,$y) out of range: ($width, $height)")

        data[x+y*width] = value
    }

    override operator fun get(x:Int, y:Int): Boolean {
        if (x !in 0 until width ||
            y !in 0 until height) throw IndexOutOfBoundsException("($x,$y) out of range: ($width, $height)")

        return data[x+y*width]
    }

    override fun toString(): String = buildString {
        for(y in 0 until height) {
            (0 until width).joinTo(this, separator = " ") {x -> if(get(x, y)) "T" else "F"}
            appendLine()
        }
    }

    override fun asSequence(): Sequence<Boolean> {
        return (this as Iterable<Boolean>).asSequence()
    }

    override fun iterator(): Iterator<Boolean> {
        return object : Iterator<Boolean> {
            var nextPos = -1

            init {
                moveToNextValidPos()
            }

            override fun hasNext(): Boolean = nextPos < data.size

            @Suppress("UNCHECKED_CAST")
            override fun next(): Boolean = data[nextPos].also {
                moveToNextValidPos()
            }

            private fun moveToNextValidPos() {
                do {
                    nextPos++
                } while (nextPos < data.size && !isValid(nextPos % maxWidth, nextPos / maxWidth))
            }
        }
    }

}