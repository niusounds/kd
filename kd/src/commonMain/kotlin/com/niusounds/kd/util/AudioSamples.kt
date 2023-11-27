package com.niusounds.kd.util

/**
 * Interleaved audio samples to Non-Interleaved audio samples.
 *
 * L1, R1, L2, R2, L3, R3, ...
 * to
 * L1, L2, L3, ... , R1, R2, R3, ...
 */
fun FloatArray.deinterleave(out: FloatArray, channels: Int) {
    check(channels > 0) { "channels must be greater than 0" }
    check(size == out.size) { "both FloatArray must have same size" }

    val frameSize = size / channels

    forEachIndexed { index, sample ->
        val ch = index % channels
        val i = index / channels
        out[frameSize * ch + i] = sample
    }
}

/**
 * Extract single channel audio samples from interleaved audio samples.
 *
 * L1, R1, L2, R2, L3, R3, ...
 * to
 * L1, L2, L3, .. (channels = 2, outChannel = 0)
 * or
 * R1, R2, R3, ... (channels = 2, outChannel = 1)
 */
fun FloatArray.extractSingleChannel(out: FloatArray, channels: Int, outChannel: Int) {
    check(channels > 0) { "channels must be greater than 0" }

    val frameSize = size / channels
    check(frameSize == out.size) { "out FloatArray must have size of $frameSize" }

    repeat(frameSize) { i ->
        out[i] = get(i * channels + outChannel)
    }
}

/**
 * Non-Interleaved audio samples to Interleaved audio samples.
 *
 * L1, L2, L3, ... , R1, R2, R3, ...
 * to
 * L1, R1, L2, R2, L3, R3, ...
 */
fun FloatArray.interleave(out: FloatArray, channels: Int) {
    check(channels > 0) { "channels must be greater than 0" }
    check(size == out.size) { "both FloatArray must have same size" }

    val frameSize = size / channels

    repeat(frameSize) { i ->
        repeat(channels) { ch ->
            out[i * channels + ch] = get(frameSize * ch + i)
        }
    }
}
