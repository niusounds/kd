package com.niusounds.kd

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class KdTest {
    @Test
    fun testKdAndNode() = runTest(UnconfinedTestDispatcher()) {
        val node = MockNode()
        val config = AudioConfig()
        val kd = Kd(config = config) {
            add(node)
        }

        // initial state
        assertNull(node.config)
        assertFalse(node.started)
        assertFalse(node.released)

        // node.configure() and node.start() will be called after kd.launch()
        val kdJob = launch {
            kd.launch()
        }

        launch {
            assertEquals(config, node.config)
            assertTrue(node.started)
            assertFalse(node.released)
        }

        kdJob.cancel()

        // node.release() will be called after cancelling kd.launch()
        launch {
            assertTrue(node.released)
        }
    }
}