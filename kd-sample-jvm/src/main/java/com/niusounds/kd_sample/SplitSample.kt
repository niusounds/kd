package com.niusounds.kd_sample

import com.niusounds.kd.Kd
import com.niusounds.kd.node.AudioInput
import com.niusounds.kd.node.Gain
import com.niusounds.kd.node.Group
import com.niusounds.kd.node.Rms
import com.niusounds.kd.node.Split
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val rms1 = Rms()
        val rms2 = Rms()
        launch {
            Kd {
                add(AudioInput())
                add(
                    Split(
                        rms1,
                        Group(
                            Gain(value = 0.1f),
                            rms2,
                        )
                    )
                )
            }.launch()
        }
        launch {
            combine(rms1.flow, rms2.flow) { rms1, rms2 -> "rms1: $rms1 rms2:$rms2" }
                .sample(1000).collect {
                    println(it)
                }
        }
    }
}
