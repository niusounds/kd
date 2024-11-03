package com.niusounds.kd

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class KdTest {
    @Test
    fun testKdAndNode() = runTest {
        val node = MockNode()
        val config = AudioConfig()
        val kd = Kd(config = config) {
            node
        }

        // initial state
        assertNull(node.config)
        assertFalse(node.started)
        assertFalse(node.released)

        // node.configure() and node.start() will be called after kd.launch()
        val kdJob = backgroundScope.launch {
            kd.launch()
        }
        runCurrent()
        advanceUntilIdle()

        assertEquals(config, node.config)
        assertTrue(node.started)
        assertFalse(node.released)

        kdJob.cancel()

        runCurrent()
        advanceUntilIdle()

        // node.release() will be called after cancelling kd.launch()
        assertTrue(node.released)
    }
}