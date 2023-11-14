package com.niusounds.kd_sample

import com.niusounds.kd.AudioConfig
import com.niusounds.kd.Kd
import com.niusounds.kd.node.AudioInput
import com.niusounds.kd.node.AudioOutput
import com.niusounds.kd.node.Gain
import com.niusounds.kd.node.StereoPanner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.WindowConstants

fun main() {
    val gain = Gain()
    val panner = StereoPanner()

    // Run audio in background
    GlobalScope.launch {
        Kd(config = AudioConfig(channels = 2)) {
            add(AudioInput())
            add(gain)
            add(panner)
            add(AudioOutput())
        }.launch()
    }

    // Show UI
    val frame = JFrame("GainNode")
    frame.isVisible = true
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.setSize(300, 300)

    val volumeLabel = JLabel("volume: 1.0")

    // Volume slider
    val volumeSlider = JSlider(0, 1000, 100)
    volumeSlider.addChangeListener { ev ->
        val volume = volumeSlider.value.toFloat() * 0.01f
        volumeLabel.text = "volume: $volume"
        gain.value = volume
    }

    val panLabel = JLabel("pan: 0.0")

    // Pan slider
    val panSlider = JSlider(-100, 100, 0)
    panSlider.addChangeListener { ev ->
        val pan = panSlider.value.toFloat() * 0.01f
        panLabel.text = "pan: $pan"
        panner.value = pan
    }

    val panel = JPanel()
    frame.contentPane.add(panel, BorderLayout.CENTER)

    panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
    panel.add(volumeLabel)
    panel.add(volumeSlider)
    panel.add(panLabel)
    panel.add(panSlider)
}
