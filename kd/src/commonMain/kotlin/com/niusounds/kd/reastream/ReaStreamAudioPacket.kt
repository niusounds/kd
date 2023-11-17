package com.niusounds.kd.reastream

class ReaStreamAudioPacket(
    val identifier: String,
    val sampleRate: Int,
    val channels: Int,
    val samples: FloatArray,
) {
    companion object {
        private val audioIdentifier = byteArrayOf(77, 82, 83, 82)

        fun readFromBytes(bytes: ByteArray): ReaStreamAudioPacket? {
            val isAudio = bytes[0] == audioIdentifier[0]
                    && bytes[1] == audioIdentifier[1]
                    && bytes[2] == audioIdentifier[2]
                    && bytes[3] == audioIdentifier[3]

            if (!isAudio) {
                return null
            }

//            val packetSize = bytes.toInt(4)

            val identifier =
                bytes.copyOfRange(fromIndex = 8, toIndex = 40)
                    .toString(Charsets.UTF_8)
                    .trim { char -> !char.isLetter() }

            val channels = bytes[40].toInt()

            val sampleRate = bytes.toInt(41)
            val blockLength = bytes.toShort(45)

            val sizeInFloats = blockLength.toInt() / Float.SIZE_BYTES
            val samples = if (channels == 1) {
                FloatArray(sizeInFloats) { index ->
                    bytes.toFloat(47 + index * Float.SIZE_BYTES)
                }
            } else {
                val out = FloatArray(sizeInFloats)
                var bytesIndex = 47
                repeat(channels) { ch ->
                    val frameSize = sizeInFloats / channels
                    repeat(frameSize) { i ->
                        val dstIndex = i * channels + ch
                        out[dstIndex] = bytes.toFloat(bytesIndex)
                        bytesIndex += Float.SIZE_BYTES
                    }
                }
                out
            }

            return ReaStreamAudioPacket(
                identifier = identifier,
                sampleRate = sampleRate,
                channels = channels,
                samples = samples
            )
        }
    }
}

//  4byte (Little Endian)
private fun ByteArray.toInt(index: Int): Int {
    var result: Int = 0
    for (i in 0..3) {
        result = result or (get(i + index).toUByte().toInt() shl 8 * i)
    }
    return result
}

//  2byte (Little Endian)
private fun ByteArray.toShort(index: Int): Short {
    var result: Int = 0
    for (i in 0..1) {
        result = result or (get(i + index).toUByte().toInt() shl 8 * i)
    }
    return result.toShort()
}

//  4byte (Little Endian)
private fun ByteArray.toFloat(index: Int): Float = Float.fromBits(toInt(index))
