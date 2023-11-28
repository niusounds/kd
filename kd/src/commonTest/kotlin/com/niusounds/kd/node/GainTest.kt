package com.niusounds.kd.node

import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class GainTest {

    @Test
    fun process() {
        val gain = Gain()
        gain.value = 2.0f

        val samples = floatArrayOf(0.1f, 0.2f, -0.5f)
        val expectedResult = floatArrayOf(0.2f, 0.4f, -1.0f)
        gain.process(samples)

        assertContentEquals(expectedResult, samples)
    }
}