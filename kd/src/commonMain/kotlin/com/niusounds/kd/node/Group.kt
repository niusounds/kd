package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node

/**
 * 加工Node
 * 一連のNodeを一塊のNodeとして扱う。
 * 最後のNodeの出力内容がこのNodeの出力となる。
 */
class Group(
    private val nodes: List<Node>,
) : Node {
    constructor(vararg nodes: Node) : this(nodes.toList())

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
}
