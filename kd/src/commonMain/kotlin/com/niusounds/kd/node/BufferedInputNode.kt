package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BufferedInputNode(
    private val bufferSize: Int = 16,
    protected val context: CoroutineContext = Dispatchers.IO,
    private val onReadyToProcess: () -> Unit = {},
    private val onUnderflow: () -> Unit = {},
) : Node {
    private lateinit var buffer: FloatArray
    private var writeIndex: Int = 0
    private var readIndex: Int = 0
    private var readyToProcess = false
    private var outputFrameSize: Int = 0

    private val channel = Channel<FloatArray>(Channel.BUFFERED)
    private var scope: CoroutineScope? = null

    override fun configure(config: AudioConfig) {
        outputFrameSize = config.frameSize * config.channels
        buffer = FloatArray(outputFrameSize * bufferSize)
    }

    override fun start() {
        scope = CoroutineScope(context).also {
            it.launch {
                channel.asyncInput()
            }
            it.launch {
                for (chunk in channel) {
                    val bufferRemainSize = buffer.size - writeIndex

                    writeIndex = if (chunk.size <= bufferRemainSize) {
                        chunk.copyInto(buffer, writeIndex)
                        writeIndex + chunk.size
                    } else {
                        // fill until end
                        val firstWriteSize = bufferRemainSize
                        chunk.copyInto(
                            destination = buffer,
                            destinationOffset = writeIndex,
                            startIndex = 0,
                            endIndex = firstWriteSize
                        )

                        // fill from start
                        val secondWriteSize = chunk.size - firstWriteSize
                        check(secondWriteSize < buffer.size) { "buffer.size is too small. more bufferSize is required!" }
                        chunk.copyInto(
                            destination = buffer,
                            destinationOffset = 0,
                            startIndex = firstWriteSize,
                            endIndex = chunk.size
                        )

                        secondWriteSize
                    }

                    if (!readyToProcess) {
                        val filledBufferSize = if (writeIndex >= readIndex) {
                            writeIndex - readIndex
                        } else {
                            (buffer.size - readIndex) + writeIndex
                        }

                        if (filledBufferSize >= outputFrameSize * bufferSize / 2) {
                            readyToProcess = true
                            onReadyToProcess()
                        }
                    }
                }
            }
        }
    }

    override fun stop() {
        cancelCoroutines()
    }

    override fun process(audio: FloatArray) {
        if (readyToProcess) {
            // check underflow
            if (writeIndex > readIndex && writeIndex - readIndex < outputFrameSize) {
                readyToProcess = false
                onUnderflow()
                return
            }

            // buffer -> audio
            buffer.copyInto(
                destination = audio,
                startIndex = readIndex,
                endIndex = readIndex + audio.size
            )
            readIndex += audio.size
            while (readIndex >= buffer.size) {
                readIndex -= buffer.size
            }
        }
    }

    protected abstract suspend fun Channel<FloatArray>.asyncInput()

    override fun release() {
        cancelCoroutines()
    }

    private fun cancelCoroutines() {
        scope?.cancel()
        scope = null
    }
}
