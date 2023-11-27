package com.niusounds.kd.util

import kotlin.test.Test
import kotlin.test.assertContentEquals

class AudioSamplesTest {
    @Test
    fun testDeinterleave() {
        val input = floatArrayOf(
            // L, R
            1.0f, 0.8f,
            0.5f, 0.2f,
            0.1f, 0.9f,
        )
        val expected = floatArrayOf(
            // L
            1.0f, 0.5f, 0.1f,
            // R
            0.8f, 0.2f, 0.9f,
        )

        val output = FloatArray(input.size)
        input.deinterleave(output, channels = 2)
        assertContentEquals(expected, output)
    }

    @Test
    fun testInterleave() {
        val input = floatArrayOf(
            // L
            1.0f, 0.5f, 0.1f,
            // R
            0.8f, 0.2f, 0.9f,
        )
        val expected = floatArrayOf(
            // L, R
            1.0f, 0.8f,
            0.5f, 0.2f,
            0.1f, 0.9f,
        )

        val output = FloatArray(input.size)
        input.interleave(output, channels = 2)
        assertContentEquals(expected, output)
    }

    @Test
    fun testExtractSingleChannel() {
        val input = floatArrayOf(
            // L, R
            1.0f, 0.8f,
            0.5f, 0.2f,
            0.1f, 0.9f,
        )
        val expectedL = floatArrayOf(
            1.0f, 0.5f, 0.1f,
        )
        val expectedR = floatArrayOf(
            0.8f, 0.2f, 0.9f,
        )

        val channels = 2
        val output = FloatArray(input.size / channels)
        input.extractSingleChannel(output, channels, outChannel = 0)
        assertContentEquals(expectedL, output)

        input.extractSingleChannel(output, channels, outChannel = 1)
        assertContentEquals(expectedR, output)
    }
}