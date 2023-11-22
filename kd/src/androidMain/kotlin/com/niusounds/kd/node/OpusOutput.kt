package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node
import com.niusounds.kd.opus.OpusEncoder
import com.niusounds.kd.util.infiniteIterator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class OpusOutput(
    private val bufferSize: Int = 16,
) : Node {
    class EncodeResult(
        val data: ByteArray,
        val size: Int,
    )

    private lateinit var encoder: OpusEncoder
    private var frameSize: Int = 0

    private lateinit var encodedBytes: Iterator<ByteArray>
    private val outputChannel = Channel<EncodeResult>(capacity = bufferSize)

    val flow: Flow<EncodeResult> = outputChannel.receiveAsFlow()

    override fun configure(config: AudioConfig) {
        frameSize = config.frameSize
        encoder = OpusEncoder(
            fs = config.sampleRate,
            channels = config.channels,
            application = OpusEncoder.Application.AUDIO
        )
        encodedBytes = List(bufferSize) { ByteArray(1276) }.infiniteIterator()
    }

    override fun process(audio: FloatArray) {
        val encoded = encodedBytes.next()
        val encodeSize = encoder.encode(audio, frameSize, encoded, encoded.size)
        if (encodeSize > 0) {
            outputChannel.trySend(
                EncodeResult(
                    data = encoded,
                    size = encodeSize,
                )
            )
        }
    }
}
