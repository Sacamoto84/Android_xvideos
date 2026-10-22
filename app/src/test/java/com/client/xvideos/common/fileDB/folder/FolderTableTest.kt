package com.client.xvideos.common.fileDB.folder

import com.client.xvideos.common.fileDB.writeTextAtomically
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FolderTableTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun `dot keys stay inside the table directory`() = runBlocking {
        val root = tmp.newFolder("db")
        val sibling = File(root, "keep.txt").apply { writeText("data") }
        val table = FolderTable(File(root, "table").path)

        table.upsert("..", mapOf("text" to "dots"))
        assertEquals("dots", table.get("..")?.fields?.get("text"))
        assertEquals(listOf(".."), table.all().map { it.key })

        table.delete("..")
        assertTrue("родительская папка не должна удаляться", sibling.exists())
        assertTrue(table.all().isEmpty())
    }

    @Test
    fun `atomic write replaces content and leaves no temp file`() {
        val file = File(tmp.newFolder("atomic"), "item.json")
        file.writeTextAtomically("first")
        file.writeTextAtomically("second")

        assertEquals("second", file.readText())
        assertFalse(File(file.parentFile, "item.json.tmp").exists())
    }
}
