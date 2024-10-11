package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.reastream.ReaStreamAudioPacket
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.io.readByteArray
import kotlin.coroutines.CoroutineContext

/**
 * 入力Node
 * ReaStream VSTプラグインからの音声を受け取り、出力する。
 */
class ReaStreamInput(
    private val port: Int = 58710,
    private val identifier: String = "default",
    bufferSize: Int = 16,
    context: CoroutineContext = Dispatchers.IO,
) : CoroutineInputNode(bufferSize, context) {
    override suspend fun Channel<FloatArray>.process(config: AudioConfig) {
        val server = aSocket(ActorSelectorManager(context))
            .udp()
            .bind(InetSocketAddress("", port))

        for (data in server.incoming) {
            val bytes = data.packet.readByteArray()
            val packet = ReaStreamAudioPacket.readFromBytes(bytes)
            val samples = packet?.samples ?: continue
            if (packet.identifier == identifier && packet.sampleRate == config.sampleRate && packet.channels == config.channels) {
                send(samples)
            }
        }
    }
}
