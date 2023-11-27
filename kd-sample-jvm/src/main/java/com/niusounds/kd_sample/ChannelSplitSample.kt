package com.niusounds.kd_sample

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Kd
import com.niusounds.kd.node.AudioOutput
import com.niusounds.kd.node.ChannelSplit
import com.niusounds.kd.node.SinWave
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        Kd(config = AudioConfig(channels = 2, sampleRate = 44100)) {
            add(ChannelSplit { channel ->
                SinWave().apply { gain = 0.1f; frequency = 440f * (channel + 1) }
            })
            add(AudioOutput())
        }.launch()
    }
}
