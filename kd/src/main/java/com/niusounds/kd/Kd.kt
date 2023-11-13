package com.niusounds.kd

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

data class AudioConfig(
    val sampleRate: Int = 48000,
    val channels: Int = 1,
    val frameSize: Int = 960,
)

interface Node {
    fun configure(config: AudioConfig)
    fun start()
    fun process(audio: FloatArray)
    fun stop()
    fun release()
}

fun Kd(
    config: AudioConfig = AudioConfig(),
    setup: Kd.() -> Unit,
): Kd {
    return Kd(config).apply(setup)
}

class Kd(
    private val config: AudioConfig,
) {
    private var nodes = listOf<Node>()
    private val stopped = MutableStateFlow(false)

    fun add(node: Node) {
        nodes += node
    }

    fun start() {
        stopped.value = false
    }

    fun stop() {
        stopped.value = true
    }

    private val executor = Executors.newSingleThreadExecutor()
    private val coroutineContext = executor.asCoroutineDispatcher()

    suspend fun launch() {
        withContext(coroutineContext) {
            try {
                val audio = FloatArray(config.frameSize * config.channels)

                nodes.forEach { it.configure(config) }
                nodes.forEach { it.start() }

                while (coroutineContext.isActive) {

                    if (stopped.value) {
                        nodes.forEach { it.stop() }
                        stopped.first { !it } // wait until stopped.value = false
                        nodes.forEach { it.start() }
                    }

                    nodes.forEach { it.process(audio) }
                }
            } finally {
                nodes.forEach { it.release() }
            }
        }
    }

    fun release() {
        stop()
        executor.shutdown()
    }
}
