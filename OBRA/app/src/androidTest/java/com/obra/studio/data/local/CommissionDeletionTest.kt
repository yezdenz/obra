package com.obra.studio.data.local

import android.content.Context
import android.database.SQLException
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.obra.studio.data.CommissionDraft
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

/** Isolated test data only; never opens the artist's database. */
@RunWith(AndroidJUnit4::class)
class CommissionDeletionTest {
    private lateinit var context: Context
    private lateinit var name: String
    private lateinit var helper: ObraDatabaseHelper
    private lateinit var repository: SqliteStudioRepository
    private val files=mutableListOf<File>()
    private val children=listOf("payments","artwork_updates","attachments","commission_tags","checklist_items")

    @Before fun setup() {
        context=ApplicationProvider.getApplicationContext()
        name="deletion-test-${UUID.randomUUID()}.db"
        helper=ObraDatabaseHelper(context,name)
        repository=SqliteStudioRepository(helper,context)
    }

    @After fun cleanup() {
        helper.close();context.deleteDatabase(name);files.forEach { it.delete() }
    }

    private fun token()=UUID.randomUUID().toString()
    private fun create(image: String?=null): Long = repository.saveCommission(CommissionDraft(
        username="shared.delete.client",title="Deletion test",genre="Fantasy",tags="Soft light, Lilac",
        total="2000.00",commissionOn="2026-10-01",dueOn="2026-11-10",
        images=ArrayList(listOfNotNull(image)),initialEnabled=true,initialAmount="500.00",
        initialDate="2026-10-02"),token())

    private fun count(table: String,id: Long): Long = helper.readableDatabase
        .rawQuery("SELECT COUNT(*) FROM $table WHERE commission_id=?",arrayOf(id.toString()))
        .use { it.moveToFirst();it.getLong(0) }

    private fun image(): Pair<File,String> {
        val file=File(context.filesDir,"delete-shared-${token()}.png").also { files.add(it) }
        val bitmap=Bitmap.createBitmap(16,16,Bitmap.Config.ARGB_8888)
        try { file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) } }
        finally { bitmap.recycle() }
        return file to Uri.fromFile(file).toString()
    }

    @Test fun completedHistoryDeletionRemovesItsChildrenAndPreservesOtherWorkAndImages() {
        val (file,uri)=image()
        val removed=create(uri)
        val kept=create(uri)
        repository.startCommission(removed,"2026-10-03")
        repository.updateProgress(removed,"Finished","2026-10-04")
        repository.recordPayment(removed,150000,"2026-10-04","Cash","Balance",token())
        repository.addArtwork(removed,"Sketch",uri,token())
        repository.addArtwork(removed,"Finished",uri,token())
        repository.addArtwork(kept,"Sketch",uri,token())
        helper.writableDatabase.execSQL("""INSERT INTO attachments
            (commission_id,kind,storage_kind,location,mime_type)
            VALUES(?,'FINAL','APP_FILE',?,'image/png')""",arrayOf(removed,file.name))
        val expected=repository.snapshot().commissions().single { it.id==kept }
        val expectedReceipts=repository.snapshot().payments().filter { it.commissionId==kept }
        val expectedCounts=children.associateWith { count(it,kept) }
        children.forEach { assertTrue("Seed $it",count(it,removed)>0) }

        repository.deleteCommission(removed)
        helper.close()
        helper=ObraDatabaseHelper(context,name)
        repository=SqliteStudioRepository(helper,context)
        val saved=repository.snapshot()
        assertEquals(listOf(expected),saved.commissions())
        assertEquals(expectedReceipts,saved.payments())
        assertEquals(50000L,saved.payments().sumOf { it.amountMinor })
        children.forEach {
            assertEquals("Remove only deleted artwork's $it",0L,count(it,removed))
            assertEquals("Preserve other artwork's $it",expectedCounts[it],count(it,kept))
        }
        assertTrue("Original shared image remains on disk",file.isFile)
        helper.readableDatabase.rawQuery("SELECT COUNT(*) FROM clients",null).use {
            assertTrue(it.moveToFirst());assertEquals(1,it.getInt(0))
        }
        assertTrue(saved.activeTypes.contains("Portrait"))
        assertTrue(saved.activeGenres.contains("Fantasy"))
        helper.readableDatabase.rawQuery("PRAGMA foreign_key_check",null).use { assertFalse(it.moveToFirst()) }
        helper.readableDatabase.rawQuery("PRAGMA integrity_check",null).use {
            assertTrue(it.moveToFirst());assertEquals("ok",it.getString(0))
        }
    }

    @Test fun queuedCurrentCompletedAndCancelledCommissionsCanBeDeleted() {
        listOf("Queued","In progress","Completed","Cancelled").forEach { status ->
            val id=create()
            if(status=="In progress" || status=="Completed")repository.startCommission(id,"2026-10-03")
            if(status=="Completed")repository.updateProgress(id,"Finished","2026-10-04")
            if(status=="Cancelled")helper.writableDatabase.execSQL(
                "UPDATE commissions SET status='CANCELLED',cancelled_on='2026-10-03' WHERE id=?",arrayOf(id))
            assertEquals(status,repository.snapshot().commissions().single().status)
            repository.deleteCommission(id)
            assertTrue(repository.snapshot().commissions().isEmpty())
            assertTrue(repository.snapshot().payments().isEmpty())
        }
    }

    @Test fun failedParentDeleteRollsBackAllRemovedChildren() {
        val (_,uri)=image()
        val id=create(uri)
        repository.addArtwork(id,"Sketch",uri,token())
        val before=repository.snapshot()
        val counts=children.associateWith { count(it,id) }
        helper.writableDatabase.execSQL("""CREATE TRIGGER block_test_delete
            BEFORE DELETE ON commissions BEGIN SELECT RAISE(ABORT,'Test rollback'); END""")
        try {
            repository.deleteCommission(id)
            fail("Expected the test trigger to reject deletion")
        } catch (_: SQLException) { }
        val after=repository.snapshot()
        assertEquals(before.commissions(),after.commissions())
        assertEquals(before.payments(),after.payments())
        children.forEach { assertEquals("Restore $it after failure",counts[it],count(it,id)) }
    }

    @Test fun missingCommissionDoesNotDeleteOtherRecords() {
        create()
        val before=repository.snapshot()
        try {
            repository.deleteCommission(Long.MAX_VALUE)
            fail("Expected missing record rejection")
        } catch (_: IllegalArgumentException) { }
        assertEquals(before.commissions(),repository.snapshot().commissions())
        assertEquals(before.payments(),repository.snapshot().payments())
    }
}
