package com.niusounds.kd.opus

import com.niusounds.kd.util.loadNative

class OpusEncoder(
    fs: Int,
    channels: Int,
    application: Application,
) {
    enum class Application(internal val rawValue: Int) {
        VOIP(2048),
        AUDIO(2049),
    }

    private var nativePointer: Long = 0

    init {
        nativePointer = create(fs, channels, application.rawValue)
    }

    fun encode(
        pcm: FloatArray,
        frameSize: Int,
        dataByteArray: ByteArray,
        maxDataBytes: Int
    ): Int = encodeFloat(nativePointer, pcm, frameSize, dataByteArray, maxDataBytes)

    fun destroy() {
        if (nativePointer != 0L) {
            destroy(nativePointer)
            nativePointer = 0
        }
    }

    private external fun create(fs: Int, channels: Int, application: Int): Long
    private external fun destroy(ptr: Long)
    private external fun encodeFloat(
        ptr: Long,
        pcm: FloatArray,
        frameSize: Int,
        dataByteArray: ByteArray,
        maxDataBytes: Int
    ): Int

    companion object {
        init {
            loadNative("opus_jni")
        }
    }
}
