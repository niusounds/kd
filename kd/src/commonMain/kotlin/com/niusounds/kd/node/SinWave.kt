package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node
import kotlin.math.PI
import kotlin.math.sin

class SinWave : Node {
    var frequency: Float = 440f
    var gain: Float = 1.0f

    private var channels = 0
    private var t: Double = 0.0
    private var dt: Double = 0.0

    override fun configure(config: AudioConfig) {
        channels = config.channels
        dt = 1.0 / config.sampleRate.toDouble()
    }

    override fun start() {
    }

    override fun process(audio: FloatArray) {
        for (i in audio.indices step channels) {
            val sample = sin(t * frequency * 2 * PI).toFloat() * gain
            repeat(channels) { ch ->
                audio[i + ch] = sample
            }
            t += dt
        }
    }

    override fun stop() {
    }

    override fun release() {
    }
}