package com.niusounds.kd.node

import com.niusounds.kd.Node
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.log10
import kotlin.math.sqrt

/**
 * [process]ごとの音声データのRMSのデシベル値を計算して[flow]に流す。
 */
class Rms : Node {
    private val value = MutableStateFlow(0.0f)
    val flow: StateFlow<Float> = value.asStateFlow()

    override fun process(audio: FloatArray) {
        val squareSum = audio.sumOf { it.toDouble() * it.toDouble() }
        value.value = 20 * log10(sqrt(squareSum.toFloat()))
    }
}
