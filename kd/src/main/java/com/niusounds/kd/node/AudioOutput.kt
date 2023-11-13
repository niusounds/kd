package com.niusounds.kd.node

import android.media.AudioFormat
import android.media.AudioTrack
import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node

class AudioOutput : Node {

    private lateinit var audioTrack: AudioTrack

    override fun configure(config: AudioConfig) {

        val channelConfig = channelMaskOf(config.channels)
        val audioFormat = AudioFormat.ENCODING_PCM_FLOAT
        val minBufferSize = AudioTrack.getMinBufferSize(
            config.sampleRate,
            channelConfig,
            audioFormat,
        )

        audioTrack = AudioTrack.Builder()
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(config.sampleRate)
                    .setChannelMask(channelConfig)
                    .setEncoding(audioFormat)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize)
            .build()
    }

    private fun channelMaskOf(channels: Int): Int {
        return when (channels) {
            1 -> AudioFormat.CHANNEL_OUT_MONO
            2 -> AudioFormat.CHANNEL_OUT_STEREO
            else -> error("Not supported channels:$channels")
        }
    }

    override fun start() {
        audioTrack.play()
    }

    override fun process(audio: FloatArray) {
        audioTrack.write(audio, 0, audio.size, AudioTrack.WRITE_BLOCKING)
    }

    override fun stop() {
        audioTrack.stop()
    }

    override fun release() {
        audioTrack.release()
    }
}
