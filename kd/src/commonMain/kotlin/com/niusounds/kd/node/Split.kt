package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node

/**
 * 直前のNodeからの入力を複数のNodeに枝分かれさせる。
 * [nodes]間の[process]出力はお互いに影響しない。
 * このNode自体は入力を加工しない。
 */
class Split(
    private val nodes: List<Node>,
) : Node {
    constructor(vararg nodes: Node) : this(nodes.toList())

    private lateinit var tempBuffers: List<FloatArray>

    override fun configure(config: AudioConfig) {
        tempBuffers = List(nodes.size) { FloatArray(config.frameSize * config.channels) }
        nodes.forEach { it.configure(config) }
    }

    override fun start() {
        nodes.forEach { it.start() }
    }

    override fun process(audio: FloatArray) {
        tempBuffers.forEachIndexed { index, tempBuffer ->
            audio.copyInto(tempBuffer)
            nodes[index].process(tempBuffer)
        }
    }

    override fun stop() {
        nodes.forEach { it.stop() }
    }

    override fun release() {
        nodes.forEach { it.release() }
    }
}