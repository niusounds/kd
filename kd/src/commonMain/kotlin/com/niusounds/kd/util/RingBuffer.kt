package com.niusounds.kd.util

class RingBuffer(
    size: Int,
) {
    private val buffer = FloatArray(size)

    /**
     * [buffer]の次に書き込む位置を指す。
     */
    private var writeIndex: Int = 0

    /**
     * [buffer]の次に読み取る位置を指す。
     */
    private var readIndex: Int = 0

    val filledBufferSize: Int
        get() = if (writeIndex >= readIndex) {
            writeIndex - readIndex
        } else {
            (buffer.size - readIndex) + writeIndex
        }

    fun write(chunk: FloatArray) {
        check(chunk.size < buffer.size) { "size is too small! size must be larger than ${chunk.size}" }

        val bufferRemainSize = buffer.size - writeIndex

        writeIndex = if (chunk.size <= bufferRemainSize) {
            chunk.copyInto(buffer, writeIndex)
            writeIndex + chunk.size
        } else {
            // fill until end
            val firstWriteSize = bufferRemainSize
            chunk.copyInto(
                destination = buffer,
                destinationOffset = writeIndex,
                startIndex = 0,
                endIndex = firstWriteSize
            )

            // fill from start
            val secondWriteSize = chunk.size - firstWriteSize
            check(secondWriteSize < buffer.size) { "size is too small! size must be larger than $secondWriteSize" }
            chunk.copyInto(
                destination = buffer,
                destinationOffset = 0,
                startIndex = firstWriteSize,
                endIndex = chunk.size
            )

            secondWriteSize
        }
    }

    /**
     * [out]に記録済みバッファの内容を書き込む。
     * [out]を全て埋めることができたらtrueを返す。
     * [out]を全て埋められるほどデータが溜まっていない場合はfalseを返す。
     */
    fun read(out: FloatArray): Boolean {
        // check underflow
        if (writeIndex > readIndex && writeIndex - readIndex < out.size) {
            return false
        }

        buffer.copyInto(
            destination = out,
            startIndex = readIndex,
            endIndex = readIndex + out.size
        )
        readIndex += out.size
        if (readIndex >= buffer.size) {
            readIndex -= buffer.size
        }

        return true
    }
}
