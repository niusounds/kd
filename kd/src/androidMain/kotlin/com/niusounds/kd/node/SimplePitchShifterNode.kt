package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node
import com.niusounds.kd.wdl.SimplePitchShifter

class SimplePitchShifterNode : Node {
    private lateinit var pitchShifter: SimplePitchShifter
    private var frameSize: Int = 0

    var pitch: Double = 1.0
        set(value) {
            field = value
            if (::pitchShifter.isInitialized) {
                pitchShifter.setShift(value)
            }
        }

    override fun configure(config: AudioConfig) {
        frameSize = config.frameSize
        pitchShifter = SimplePitchShifter(config.frameSize).apply {
            setNch(config.channels)
            setSrate(config.sampleRate.toDouble())
            setShift(pitch)
            prepare()
        }
    }

    override fun process(audio: FloatArray) {
        pitchShifter.write(audio, audio.size)
        pitchShifter.bufferDone(frameSize)
        pitchShifter.getSamples(audio, audio.size)
    }

    override fun release() {
        pitchShifter.release()
    }
}