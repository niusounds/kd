package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.reastream.ReaStreamAudioPacket
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext

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
            val bytes = data.packet.readBytes()
            val packet = ReaStreamAudioPacket.readFromBytes(bytes)
            val samples = packet?.samples ?: continue
            if (packet.identifier == identifier && packet.sampleRate == config.sampleRate && packet.channels == config.channels) {
                send(samples)
            }
        }
    }
}
