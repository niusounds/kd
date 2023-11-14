package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node
import kotlin.random.Random

class WhiteNoise : Node {
    var gain: Float = 1.0f

    override fun configure(config: AudioConfig) {

    }

    override fun start() {
    }

    override fun process(audio: FloatArray) {
        for (i in audio.indices) {
            audio[i] = (Random.nextFloat() - 1.0f) * 2.0f * gain
        }
    }

    override fun stop() {
    }

    override fun release() {
    }
}