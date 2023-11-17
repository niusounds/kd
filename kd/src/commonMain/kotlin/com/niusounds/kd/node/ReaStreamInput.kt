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
    onReadyToProcess: () -> Unit = {},
    onUnderflow: () -> Unit = {},
) : BufferedInputNode(bufferSize, context, onReadyToProcess, onUnderflow) {
    private var configuredSampleRate: Int = 0
    private var configuredChannels: Int = 0

    override fun configure(config: AudioConfig) {
        super.configure(config)
        configuredSampleRate = config.sampleRate
        configuredChannels = config.channels
    }

    override suspend fun Channel<FloatArray>.asyncInput() {
        val server = aSocket(ActorSelectorManager(context))
            .udp()
            .bind(InetSocketAddress("", port))

        for (data in server.incoming) {
            val bytes = data.packet.readBytes()
            val packet = ReaStreamAudioPacket.readFromBytes(bytes)
            val samples = packet?.samples ?: continue
            if (packet.identifier == identifier && packet.sampleRate == configuredSampleRate && packet.channels == configuredChannels) {
                send(samples)
            }
        }
    }
}
