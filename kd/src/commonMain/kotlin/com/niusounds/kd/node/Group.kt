package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node

class Group(
    private val nodes: List<Node>,
) : Node {
    override fun configure(config: AudioConfig) {
        nodes.forEach { it.configure(config) }
    }

    override fun start() {
        nodes.forEach { it.start() }
    }

    override fun process(audio: FloatArray) {
        nodes.forEach { it.process(audio) }
    }

    override fun stop() {
        nodes.forEach { it.stop() }
    }

    override fun release() {
        nodes.forEach { it.release() }
    }

    operator fun plus(node: Node): Group {
        return Group(nodes + node)
    }
}
