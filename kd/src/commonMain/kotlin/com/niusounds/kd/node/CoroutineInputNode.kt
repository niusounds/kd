package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node
import com.niusounds.kd.util.RingBuffer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * 入力Node
 * Coroutinesにより非同期に音声データを読み取る。
 */
abstract class CoroutineInputNode(
    private val bufferSize: Int = 16,
    protected val context: CoroutineContext = Dispatchers.IO,
) : Node {
    private lateinit var config: AudioConfig

    /**
     * [process]により非同期に書き込まれ、サンプルデータを貯めておくバッファ。
     */
    private lateinit var buffer: RingBuffer

    /**
     * [process]で[buffer]の音声を処理するかどうか。
     * falseである間に[buffer]にサンプルが追加されても[process]はデータを処理しない。
     * [buffer]に一定以上のサンプルが貯まるとtrueになり処理が開始する。これは[waitingFrameSize]で表現される。
     */
    private val processing = MutableStateFlow(false)

    /**
     * このフレーム数分[buffer]が埋まるまでは処理を開始しない。
     */
    private var waitingFrameSize: Int = 0

    private val channel = Channel<FloatArray>(Channel.BUFFERED)
    private val scope = CoroutineScope(context)
    private var job: Job? = null

    override fun configure(config: AudioConfig) {
        this.config = config
        val outputFrameSize = config.frameSize * config.channels
        buffer = RingBuffer(outputFrameSize * bufferSize)
        waitingFrameSize = outputFrameSize * bufferSize / 2
    }

    override fun start() {
        job = scope.launch {
            launch {
                channel.process(config)
            }
            launch {
                for (chunk in channel) {
                    buffer.write(chunk)

                    // 十分バッファが溜まっていたら処理を開始
                    if (!processing.value) {
                        if (buffer.filledBufferSize >= waitingFrameSize) {
                            processing.value = true
                        }
                    }
                }
            }
        }
    }

    override fun stop() {
        job?.cancel()
        job = null
    }

    override fun process(audio: FloatArray) {
        if (processing.value) {
            if (!buffer.read(audio)) {
                // アンダーフローしたら処理を止める
                processing.value = false
                return
            }
        }
    }

    /**
     * [config]に合致する音声データを[Channel.send]で送信し続ける処理。
     */
    abstract suspend fun Channel<FloatArray>.process(config: AudioConfig)

    override fun release() {
        scope.cancel()
    }
}
