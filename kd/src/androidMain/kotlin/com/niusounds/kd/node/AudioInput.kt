package com.niusounds.kd.node

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node

actual class AudioInput actual constructor(
    private val source: Source,
) : Node {

    private lateinit var audioRecord: AudioRecord

    @SuppressLint("MissingPermission")
    override fun configure(config: AudioConfig) {
        val channelConfig = channelMaskOf(config.channels)
        val audioFormat = AudioFormat.ENCODING_PCM_FLOAT
        val minBufferSize = AudioRecord.getMinBufferSize(
            config.sampleRate,
            channelConfig,
            audioFormat,
        )

        audioRecord = AudioRecord.Builder()
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(config.sampleRate)
                    .setChannelMask(channelConfig)
                    .setEncoding(audioFormat)
                    .build()
            )
            .setAudioSource(sourceOf(source))
            .setBufferSizeInBytes(minBufferSize)
            .build()
    }

    private fun sourceOf(source: Source): Int {
        return when (source) {
            Source.Default -> MediaRecorder.AudioSource.DEFAULT
            Source.VoiceCommunication -> MediaRecorder.AudioSource.VOICE_COMMUNICATION
        }
    }

    private fun channelMaskOf(channels: Int): Int {
        return when (channels) {
            1 -> AudioFormat.CHANNEL_IN_MONO
            2 -> AudioFormat.CHANNEL_IN_STEREO
            else -> error("Not supported channels:$channels")
        }
    }

    override fun start() {
        audioRecord.startRecording()
    }

    override fun process(audio: FloatArray) {
        audioRecord.read(audio, 0, audio.size, AudioRecord.READ_BLOCKING)
    }

    override fun stop() {
        audioRecord.stop()
    }

    override fun release() {
        audioRecord.release()
    }
}
