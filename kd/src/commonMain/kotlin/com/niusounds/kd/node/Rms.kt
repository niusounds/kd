package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.log10
import kotlin.math.sqrt

class Rms : Node {
    private val value = MutableStateFlow(0.0f)
    val flow: StateFlow<Float> = value.asStateFlow()

    override fun configure(config: AudioConfig) {
    }

    override fun start() {
    }

    override fun process(audio: FloatArray) {
        val squareSum = audio.map { it * it }.sum()
        value.value = 20 * log10(sqrt(squareSum))
    }

    override fun stop() {
    }

    override fun release() {
    }
}
