package com.niusounds.kd.node

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Node

/**
 * 加工Node
 * ステレオ音声のパンニングを行う。
 * チャンネル数が2の場合のみ動作する。
 */
class StereoPanner : Node {
    var value: Float = 0.0f

    private var enabled = false

    override fun configure(config: AudioConfig) {
        enabled = config.channels == 2
    }

    override fun process(audio: FloatArray) {
        if (!enabled) return

        if (value != 0.0f) {
            for (i in audio.indices step 2) {
                // L channel
                // -1.0 ~ -0.5 ~ 0.0 ~ 0.5 ~ 1.0
                //  1.0 ~  1.0 ~ 0.0 ~ 0.5 ~ 0.0
                audio[i] *= (1.0f - value).coerceIn(0.0f, 1.0f)
                // R channel
                // -1.0 ~ -0.5 ~ 0.0 ~ 0.5 ~ 1.0
                //  0.0 ~  0.5 ~ 1.0 ~ 1.0 ~ 1.0
                audio[i + 1] *= (1.0f + value).coerceIn(0.0f, 1.0f)
            }
        }
    }
}
