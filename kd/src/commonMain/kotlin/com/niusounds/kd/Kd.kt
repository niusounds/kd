package com.niusounds.kd

import com.niusounds.kd.node.Group
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

data class AudioConfig(
    val sampleRate: Int = 48000,
    val channels: Int = 1,
    val frameSize: Int = 960,
)

/**
 * 音声処理の1単位。各Nodeの役割は大きく入力・出力・加工に大別される。
 */
interface Node {
    /**
     * [Kd.launch]により音声処理が開始される時に呼ばれる。
     * [process]で使用する情報を[config]から読み取り、フィールドなどに覚えておく。
     */
    fun configure(config: AudioConfig) {}

    /**
     * [Kd.launch]により音声処理が開始される時および[Kd.start]で一時停止された処理を再開する時に呼ばれる。
     */
    fun start() {}

    /**
     * 入力Nodeは[audio]に音声を書き込む。
     * 出力Nodeは[audio]の内容を読み取り、処理をする。
     * 加工Nodeは[audio]から音声を読み取り、[audio]に加工後の音声を書き戻す。
     * [audio]の長さは[configure]に渡される[AudioConfig]の `frameSize * channels` で計算できる。
     */
    fun process(audio: FloatArray)

    /**
     * [Kd.stop]により音声処理が一時停止される時に呼ばれる。
     */
    fun stop() {}

    /**
     * [Kd.launch]を呼び出したコルーチンスコープがキャンセルされ、音声処理が終了された時に呼ばれる。
     */
    fun release() {}

    operator fun plus(other: Node): Node {
        return Group(this, other)
    }
}

fun Kd(
    config: AudioConfig = AudioConfig(),
    coroutineContext: CoroutineContext = Dispatchers.IO,
    setup: () -> Node,
): Kd {
    return Kd(
        root = setup(),
        config = config,
        coroutineContext = coroutineContext
    )
}

class Kd(
    private val root: Node,
    private val config: AudioConfig,
    private val coroutineContext: CoroutineContext,
) {
    private val stopped = MutableStateFlow(false)

    fun start() {
        stopped.value = false
    }

    fun stop() {
        stopped.value = true
    }

    suspend fun launch() {
        withContext(coroutineContext) {
            try {
                val audio = FloatArray(config.frameSize * config.channels)

                root.configure(config)
                root.start()

                while (coroutineContext.isActive) {

                    if (stopped.value) {
                        root.stop()
                        stopped.first { !it } // wait until stopped.value = false
                        root.start()
                    }

                    root.process(audio)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                root.release()
            }
        }
    }
}
