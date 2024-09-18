package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node
import com.niusounds.kd.util.extractSingleChannel
import com.niusounds.kd.util.writeSingleChannel

/**
 * 加工Node
 * 前のNodeからの音声をチャンネルごとに分けて各チャンネルごとに異なるNodeを適用させて出力する。
 */
class ChannelSplit(
    private val channelNodeBuilder: (channel: Int) -> Node
) : Node {
    private var frameSize: Int = 0
    private var channels: Int = 0
    private lateinit var nodes: List<Node>
    private lateinit var singleChannelAudio: FloatArray

    override fun configure(config: AudioConfig) {
        frameSize = config.frameSize
        channels = config.channels

        val monoConfig = config.copy(channels = 1)
        nodes = List(config.channels, channelNodeBuilder)
        nodes.forEach { it.configure(monoConfig) }

        singleChannelAudio = FloatArray(frameSize)
    }

    override fun start() {
        nodes.forEach { it.start() }
    }

    override fun process(audio: FloatArray) {
        nodes.forEachIndexed { ch, node ->
            audio.extractSingleChannel(
                singleChannelAudio,
                channels = channels,
                outChannel = ch,
            )
            node.process(singleChannelAudio)
            audio.writeSingleChannel(
                singleChannelAudio,
                allChannels = channels,
                writeChannel = ch,
            )
        }
    }

    override fun stop() {
        nodes.forEach { it.stop() }
    }

    override fun release() {
        nodes.forEach { it.release() }
    }
}
