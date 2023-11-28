package com.niusounds.kd.util

/**
 * Store samples for processing.
 *
 * [0] is most recent sample.
 * [1] is previous sample.
 * [size-1] is most oldest sample.
 */
class CircularBuffer(
    val size: Int
) {
    private val buffer = FloatArray(size)
    private val mask = size - 1
    private var top = 0

    init {
        check(size % 2 == 0) { "size must be power of 2" }
    }

    fun insert(value: Float) {
        top--
        top = top and mask
        buffer[top] = value
    }

    fun insert(values: FloatArray) {
        check(values.size < size) { "values array is too large. CircularBuffer's size must be larger than:${values.size}" }
        top = (top - values.size) and mask

        val bufferRemaining = size - top
        if (bufferRemaining >= values.size) {
            values.copyInto(buffer, destinationOffset = top)
        } else {
            values.copyInto(buffer, destinationOffset = top, endIndex = bufferRemaining)
            values.copyInto(
                buffer,
                destinationOffset = 0,
                startIndex = bufferRemaining,
            )
        }
    }

    operator fun get(index: Int): Float {
        return buffer[(top + index) and mask]
    }
}
