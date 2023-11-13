package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine

actual class AudioOutput : Node {
    private lateinit var sourceDataLine: SourceDataLine
    private lateinit var writeShortBuffer: ShortBuffer
    private lateinit var writeBufferArray: ByteArray

    override fun configure(config: AudioConfig) {
        val audioFormat = AudioFormat(
            config.sampleRate.toFloat(),
            16,
            config.channels,
            true,
            true,
        )

        val readBuffer = ByteBuffer.allocate(config.frameSize * config.channels * Short.SIZE_BYTES)
            .order(ByteOrder.BIG_ENDIAN)
        writeBufferArray = readBuffer.array()
        writeShortBuffer = readBuffer.asShortBuffer()

        sourceDataLine = AudioSystem.getSourceDataLine(audioFormat)
        sourceDataLine.open(audioFormat)
    }

    override fun start() {
        sourceDataLine.start()
    }

    override fun process(audio: FloatArray) {
        writeShortBuffer.position(0)
        for (i in audio.indices) {
            writeShortBuffer.put((audio[i] * Short.MAX_VALUE).toInt().toShort())
        }
        sourceDataLine.write(writeBufferArray, 0, writeBufferArray.size)
    }

    override fun stop() {
        sourceDataLine.stop()
    }

    override fun release() {
        sourceDataLine.close()
    }
}
