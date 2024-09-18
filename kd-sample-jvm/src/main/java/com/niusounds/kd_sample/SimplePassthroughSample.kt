package com.niusounds.kd_sample

import com.niusounds.kd.Kd
import com.niusounds.kd.node.AudioInput
import com.niusounds.kd.node.AudioOutput
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        Kd {
            AudioInput() + AudioOutput()
        }.launch()
    }
}
