package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node

class Gain : Node {
    var value: Float = 1.0f

    override fun configure(config: AudioConfig) {
    }

    override fun start() {
    }

    override fun process(audio: FloatArray) {
        if (value != 1f) {
            for (i in audio.indices) {
                audio[i] *= value
            }
        }
    }

    override fun stop() {
    }

    override fun release() {
    }
}
