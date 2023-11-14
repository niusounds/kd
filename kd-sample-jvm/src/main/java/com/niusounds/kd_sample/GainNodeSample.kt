package com.niusounds.kd_sample

import com.niusounds.kd.Kd
import com.niusounds.kd.node.AudioInput
import com.niusounds.kd.node.AudioOutput
import com.niusounds.kd.node.Gain
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JSlider
import javax.swing.WindowConstants

fun main() {
    val gain = Gain()

    // Run audio in background
    GlobalScope.launch {
        Kd {
            add(AudioInput())
            add(gain)
            add(AudioOutput())
        }.launch()
    }

    // Show UI
    val frame = JFrame("GainNode")
    frame.isVisible = true
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.setSize(300, 100)

    val volumeLabel = JLabel("volume: 1.0")

    // Volume slider
    val slider = JSlider(0, 1000, 100)
    slider.addChangeListener { ev ->
        val volume = slider.value.toFloat() * 0.01f
        volumeLabel.text = "volume: $volume"
        gain.value = volume
    }

    val layout = BorderLayout()
    frame.layout = layout
    frame.contentPane.add(volumeLabel, BorderLayout.NORTH)
    frame.contentPane.add(slider)
}
