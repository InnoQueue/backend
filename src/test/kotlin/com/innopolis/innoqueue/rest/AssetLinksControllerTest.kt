package com.innopolis.innoqueue.rest

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class AssetLinksControllerTest {

    @Test
    fun `Test assetLinks is not provided`() {
        val controller = AssetLinksController()
        assertThrows<RuntimeException> { controller.get() }
    }

    @Test
    fun `Test assetLinks default value is provided`() {
        val controller = AssetLinksController("null")
        assertThrows<RuntimeException> { controller.get() }
    }

    @Test
    fun `Test assetLinks is provided`() {
        val controller = AssetLinksController("test")
        assertDoesNotThrow {
            val response = controller.get()
            assertEquals("test", response)
        }
    }
}
