package com.niusounds.kd.node

import com.niusounds.kd.Node

/**
 * 加工Node
 * 音声を[value]倍する。
 */
class Gain(
    var value: Float = 1.0f
) : Node {

    override fun process(audio: FloatArray) {
        if (value != 1f) {
            for (i in audio.indices) {
                audio[i] *= value
            }
        }
    }
}
