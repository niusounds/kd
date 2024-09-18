package com.niusounds.kd_sample

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Kd
import com.niusounds.kd.node.AudioOutput
import com.niusounds.kd.node.ReaStreamInput
import com.niusounds.kd.node.Rms
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val rms = Rms()
        launch {
            Kd(config = AudioConfig(channels = 2, sampleRate = 48000)) {
                ReaStreamInput() + rms + AudioOutput()
            }.launch()
        }
        launch {
            rms.flow.collect {
                println(it)
            }
        }
    }
}
