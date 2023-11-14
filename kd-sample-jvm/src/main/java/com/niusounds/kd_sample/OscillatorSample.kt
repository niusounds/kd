package com.niusounds.kd_sample

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Kd
import com.niusounds.kd.node.AudioOutput
import com.niusounds.kd.node.SinWave
import com.niusounds.kd.node.WhiteNoise
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val oscType = args.getOrElse(0) { "sin" } // change this value or pass argument when running

    runBlocking {
        Kd(config = AudioConfig(channels = 2, sampleRate = 44100)) {
            when (oscType) {
                "sin" -> add(SinWave().apply { gain = 0.1f; frequency = 440f })
                "whitenoise" -> add(WhiteNoise().apply { gain = 0.1f })
            }
            add(AudioOutput())
        }.launch()
    }
}
