package com.niusounds.kd_sample

import com.niusounds.kd.Kd
import com.niusounds.kd.node.AudioInput
import com.niusounds.kd.node.AudioOutput
import com.niusounds.kd.node.OpusOutput
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val opusOutput = OpusOutput()
        launch {
            Kd {
                add(AudioInput())
                add(AudioOutput())
                add(opusOutput)
            }.launch()
        }
        launch {
            opusOutput.flow.collect { opus ->
                println(opus.size)
            }
        }
    }
}
