package com.niusounds.kd

class MockNode : Node {
    var config: AudioConfig? = null
        private set
    var started = false
        private set
    var released = false
        private set

    override fun configure(config: AudioConfig) {
        this.config = config
    }

    override fun start() {
        started = true
    }

    override fun process(audio: FloatArray) {
    }

    override fun stop() {
        started = false
    }

    override fun release() {
        released = true
    }
}