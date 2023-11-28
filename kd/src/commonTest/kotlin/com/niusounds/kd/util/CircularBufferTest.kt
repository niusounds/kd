package com.niusounds.kd.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CircularBufferTest {
    @Test
    fun testSingleValueOperation() {
        val buf = CircularBuffer(4)
        assertEquals(4, buf.size)
        assertEquals(0.0f, buf[0])
        assertEquals(0.0f, buf[1])
        assertEquals(0.0f, buf[2])
        assertEquals(0.0f, buf[3])

        buf.insert(1.0f)
        assertEquals(1.0f, buf[0])
        assertEquals(0.0f, buf[1])
        assertEquals(0.0f, buf[2])
        assertEquals(0.0f, buf[3])

        buf.insert(0.5f)
        assertEquals(0.5f, buf[0])
        assertEquals(1.0f, buf[1])
        assertEquals(0.0f, buf[2])
        assertEquals(0.0f, buf[3])

        buf.insert(0.1f)
        assertEquals(0.1f, buf[0])
        assertEquals(0.5f, buf[1])
        assertEquals(1.0f, buf[2])
        assertEquals(0.0f, buf[3])

        buf.insert(0.25f)
        assertEquals(0.25f, buf[0])
        assertEquals(0.1f, buf[1])
        assertEquals(0.5f, buf[2])
        assertEquals(1.0f, buf[3])

        buf.insert(0.9f)
        assertEquals(0.9f, buf[0])
        assertEquals(0.25f, buf[1])
        assertEquals(0.1f, buf[2])
        assertEquals(0.5f, buf[3])
    }

    @Test
    fun testMultipleValues() {
        val buf = CircularBuffer(8)

        buf.insert(floatArrayOf(1f, 2f, 3f))
        assertEquals(1f, buf[0])
        assertEquals(2f, buf[1])
        assertEquals(3f, buf[2])
        assertEquals(0.0f, buf[3])
        assertEquals(0.0f, buf[4])
        assertEquals(0.0f, buf[5])
        assertEquals(0.0f, buf[6])
        assertEquals(0.0f, buf[7])

        buf.insert(floatArrayOf(4f, 5f, 6f))
        assertEquals(4f, buf[0])
        assertEquals(5f, buf[1])
        assertEquals(6f, buf[2])
        assertEquals(1f, buf[3])
        assertEquals(2f, buf[4])
        assertEquals(3f, buf[5])
        assertEquals(0.0f, buf[6])
        assertEquals(0.0f, buf[7])

        buf.insert(floatArrayOf(7f, 8f, 9f, 10f))
        assertEquals(7f, buf[0])
        assertEquals(8f, buf[1])
        assertEquals(9f, buf[2])
        assertEquals(10f, buf[3])
        assertEquals(4f, buf[4])
        assertEquals(5f, buf[5])
        assertEquals(6f, buf[6])
        assertEquals(1f, buf[7])

        assertFailsWith<IllegalStateException> {
            // insert samples larger than CircularBuffer is not supported
            buf.insert(floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f))
        }
    }
}