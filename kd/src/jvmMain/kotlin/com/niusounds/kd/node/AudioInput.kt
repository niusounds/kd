package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.TargetDataLine

actual class AudioInput actual constructor(source: Source) : Node {
    private lateinit var targetDataLine: TargetDataLine
    private lateinit var readShortBuffer: ShortBuffer
    private lateinit var readBufferArray: ByteArray

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
        readBufferArray = readBuffer.array()
        readShortBuffer = readBuffer.asShortBuffer()

        targetDataLine = AudioSystem.getTargetDataLine(audioFormat)
        targetDataLine.open(audioFormat)
    }

    override fun start() {
        targetDataLine.start()
    }

    override fun process(audio: FloatArray) {
        targetDataLine.read(readBufferArray, 0, readBufferArray.size)
        readShortBuffer.position(0)
        for (i in audio.indices) {
            audio[i] = readShortBuffer.get().toFloat() / Short.MAX_VALUE.toFloat()
        }
    }

    override fun stop() {
        targetDataLine.stop()
    }

    override fun release() {
        targetDataLine.close()
    }
}
