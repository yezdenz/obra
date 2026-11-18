package com.obra.studio.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.obra.studio.data.Commission
import com.obra.studio.data.CommissionDraft
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class SqliteStudioRepositoryTest {
    private lateinit var context: Context
    private lateinit var name: String
    private lateinit var helper: ObraDatabaseHelper
    private lateinit var repository: SqliteStudioRepository

    @Before fun setup() {
        context = ApplicationProvider.getApplicationContext()
        name = "backend-test-${UUID.randomUUID()}.db"
        helper = ObraDatabaseHelper(context, name)
        repository = SqliteStudioRepository(helper, context)
    }
    @After fun cleanup() { helper.close(); context.deleteDatabase(name) }

    private fun draft(username: String = "fictional.artist") = CommissionDraft(
        username = username, displayName = "Example client", title = "Test portrait", genre = "Fantasy",
        tags = "Soft light, Lilac", brief = "Test brief", total = "2000.00",
        commissionOn = "2026-10-01", dueOn = "2026-11-10", background = true
    )
    private fun edit(c: Commission) = CommissionDraft(
        platform = c.platform, username = c.username, displayName = c.displayName, title = c.title,
        type = c.type, genre = c.genre, tags = c.tags, brief = c.brief, total = "2000.00",
        commissionOn = c.commissionOn, dueOn = c.dueOn, plannedStart = c.plannedStart,
        background = c.background, images = ArrayList(c.images), expectedRevision = c.revision
    )
    private fun token() = UUID.randomUUID().toString()
    private fun rejects(action: () -> Unit) {
        try { action(); fail("Expected validation rejection") } catch (_: IllegalArgumentException) { }
    }
    private fun count(table: String) = helper.readableDatabase.rawQuery("SELECT COUNT(*) FROM $table", null)
        .use { it.moveToFirst(); it.getInt(0) }

    @Test fun intakeIsAtomicIdempotentAndPersistsAfterReopen() {
        val d = draft().apply { initialEnabled = true; initialAmount = "500.00"; initialDate = "2026-10-31" }
        val key = token()
        val id = repository.saveCommission(d, key)
        assertEquals(id, repository.saveCommission(d, key))
        helper.close()
        helper = ObraDatabaseHelper(context, name)
        repository = SqliteStudioRepository(helper, context)
        val snapshot = repository.snapshot()
        val c = snapshot.commissions().single()
        assertEquals(id, c.id)
        assertEquals("Queued", c.status)
        assertEquals("Example client", c.displayName)
        assertEquals(2, c.tasks.size)
        assertTrue(c.background)
        assertTrue(c.tags.contains("Lilac"))
        assertEquals(50000L, snapshot.payments().single().amountMinor)
        rejects { repository.saveCommission(d.copy(title = "Different input"), key) }
        assertEquals(1, count("clients"))
    }

    @Test fun invalidInitialReceiptRollsBackClientAndEveryChild() {
        val d = draft().apply { initialEnabled = true; initialAmount = "2500.00"; initialDate = "2026-10-31" }
        rejects { repository.saveCommission(d, token()) }
        listOf("clients", "genres", "commissions", "payments", "checklist_items", "commission_tags")
            .forEach { assertEquals("Rollback $it", 0, count(it)) }
    }

    @Test fun receiptsCompletionAndReportsRemainIndependent() {
        val id = repository.saveCommission(draft(), token())
        repository.recordPayment(id, 50000, "2026-10-31", "GCash", "Deposit", token())
        repository.startCommission(id, "2026-10-02")
        repository.updateProgress(id, "Colors", null)
        repository.updateProgress(id, "Finished", "2026-11-08")
        assertEquals("Completed", repository.snapshot().commissions().single().status)
        assertFalse(repository.snapshot().commissions().single().tasks.any { it.done })
        assertEquals(50000L, repository.snapshot().payments().sumOf { it.amountMinor })
        val receiptToken = token()
        repository.recordPayment(id, 150000, "2026-11-10", "Bank transfer", "Balance", receiptToken)
        repository.recordPayment(id, 150000, "2026-11-10", "Bank transfer", "Balance", receiptToken)
        val receipts = repository.snapshot().payments()
        assertEquals(2, receipts.size)
        assertEquals(50000L, receipts.filter { it.receivedOn.startsWith("2026-10") }.sumOf { it.amountMinor })
        assertEquals(150000L, receipts.filter { it.receivedOn.startsWith("2026-11") }.sumOf { it.amountMinor })
        rejects { repository.recordPayment(id, 1, "2026-11-10", "GCash", "", token()) }
        rejects { repository.recordPayment(id, 100, "2026-11-10", "Bank transfer", "Balance", receiptToken) }
    }

    @Test fun editsPreservePaymentsAndTasksAndRejectStaleDrafts() {
        val id = repository.saveCommission(draft(), token())
        repository.recordPayment(id, 50000, "2026-10-31", "GCash", "", token())
        repository.startCommission(id, "2026-10-02")
        val c = repository.snapshot().commissions().single()
        repository.setTaskDone(id, c.tasks.first().id, true)
        val d = edit(c).apply { title = "Updated artwork" }
        repository.updateCommission(id, d)
        val updated = repository.snapshot().commissions().single()
        assertEquals("Updated artwork", updated.title)
        assertEquals("In progress", updated.status)
        assertTrue(updated.tasks.first().done)
        assertEquals(1, repository.snapshot().payments().size)
        rejects { repository.updateCommission(id, d) }
        rejects { repository.updateCommission(id, edit(updated).apply { total = "100.00" }) }
        rejects { repository.updateCommission(id, edit(updated).apply { commissionOn = "2026-11-01" }) }
    }

    @Test fun concurrentPaymentsCannotOverpay() {
        val id = repository.saveCommission(draft(), token())
        val executor = Executors.newFixedThreadPool(2)
        val start = CountDownLatch(1)
        try {
            val attempts = (1..2).map {
                executor.submit<Boolean> {
                    start.await()
                    try { repository.recordPayment(id, 150000, "2026-10-31", "GCash", "", token()); true }
                    catch (_: IllegalArgumentException) { false }
                }
            }
            start.countDown()
            assertEquals(1, attempts.count { it.get(15, TimeUnit.SECONDS) })
            assertEquals(150000L, repository.snapshot().payments().sumOf { it.amountMinor })
        } finally { executor.shutdownNow() }
    }

    @Test fun reconnectUsesSavedKeysAndUniqueClients() {
        val old = repository.saveCommission(draft("previous.client"), token())
        repository.startCommission(old, "2026-10-02")
        repository.updateProgress(old, "Finished", "2026-10-03")
        val newer = repository.saveCommission(draft("previous.client"), token())
        repository.startCommission(newer, "2026-10-02")
        repository.updateProgress(newer, "Finished", "2026-10-04")
        val current = repository.saveCommission(draft("current.client"), token())
        repository.startCommission(current, "2026-10-02")
        assertEquals(newer, repository.snapshot().recommendations(current).single().previousCommissionId)
        assertEquals(2, count("clients"))
    }

    @Test fun retiredLookupsAreRejectedForNewWorkButKeptInHistory() {
        val id = repository.saveCommission(draft(), token())
        helper.writableDatabase.execSQL("UPDATE commission_types SET is_active = 0 WHERE name = 'Portrait'")
        helper.writableDatabase.execSQL("UPDATE genres SET is_active = 0 WHERE name = 'Fantasy'")
        val snapshot = repository.snapshot()
        assertFalse(snapshot.activeTypes.contains("Portrait"))
        assertFalse(snapshot.activeGenres.contains("Fantasy"))
        rejects { repository.saveCommission(draft("new.client"), token()) }
        rejects { repository.saveCommission(draft("new.client").apply { type = "Chibi" }, token()) }
        val current = snapshot.commissions().single()
        repository.updateCommission(id, edit(current).apply { title = "Historical category retained" })
        val updated = repository.snapshot().commissions().single()
        assertEquals(current.typeId, updated.typeId)
        assertEquals(current.genreId, updated.genreId)
        assertEquals(1, count("clients"))
    }

    @Test fun appFileReferencesRoundTripWithoutMultiplyingReceipts() {
        val files = (1..3).map { java.io.File(context.filesDir, "reference-${UUID.randomUUID()}.png") }
        val bitmap = android.graphics.Bitmap.createBitmap(16, 16, android.graphics.Bitmap.Config.ARGB_8888)
        try {
            files.forEach { file -> file.outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) } }
            val locations = ArrayList(files.map { android.net.Uri.fromFile(it).toString() })
            val id = repository.saveCommission(draft().apply { images = locations }, token())
            helper.writableDatabase.execSQL("UPDATE attachments SET caption = 'Keep this caption', display_name = 'Reference' WHERE commission_id = ?", arrayOf(id))
            repository.recordPayment(id, 50000, "2026-10-31", "GCash", "", token())
            repository.recordPayment(id, 150000, "2026-11-10", "Cash", "", token())
            helper.close()
            helper = ObraDatabaseHelper(context, name)
            repository = SqliteStudioRepository(helper, context)
            val saved = repository.snapshot().commissions().single()
            repository.updateCommission(id, edit(saved).apply { title = "References preserved" })
            assertEquals(3, repository.snapshot().commissions().single().images.size)
            assertEquals(200000L, repository.snapshot().payments().sumOf { it.amountMinor })
            helper.readableDatabase.rawQuery("SELECT caption, display_name, storage_kind FROM attachments WHERE commission_id = ?", arrayOf(id.toString())).use { rows ->
                while (rows.moveToNext()) {
                    assertEquals("Keep this caption", rows.getString(0))
                    assertEquals("Reference", rows.getString(1))
                    assertEquals("APP_FILE", rows.getString(2))
                }
            }
            files.first().delete()
            assertEquals(3, repository.snapshot().commissions().single().images.size)
            rejects { repository.saveCommission(draft().apply { images = arrayListOf("file:///sdcard/outside.png") }, token()) }
        } finally { bitmap.recycle(); files.forEach { it.delete() } }
    }

    @Test fun upgradingVersionOneRetainsHistoricalRows() {
        helper.close()
        context.deleteDatabase(name)
        val legacy = object : SQLiteOpenHelper(context, name, null, 1) {
            override fun onCreate(db: SQLiteDatabase) {
                ObraDatabaseHelper.schemaStatements.forEach(db::execSQL)
                db.execSQL("INSERT INTO clients(id, platform, username) VALUES(41, 'X', 'legacy.client')")
                db.execSQL("INSERT INTO commission_types(id, name) VALUES(7, 'Portrait')")
                db.execSQL("INSERT INTO genres(id, name) VALUES(9, 'Fantasy')")
                db.execSQL("""INSERT INTO commissions(id, client_id, type_id, title, agreed_total_minor,
                    commission_on, due_on) VALUES(42, 41, 7, 'Existing artwork', 200000, '2026-10-01', '2026-11-01')""")
                db.execSQL("UPDATE commissions SET primary_genre_id = 9 WHERE id = 42")
                db.execSQL("""INSERT INTO payments(commission_id, amount_minor, received_on, method)
                    VALUES(42, 50000, '2026-10-02', 'GCASH')""")
                db.execSQL("INSERT INTO commission_tags VALUES(42, 'Legacy style')")
                db.execSQL("INSERT INTO checklist_items(commission_id, label, is_done) VALUES(42, 'Legacy task', 1)")
                db.execSQL("""INSERT INTO attachments(commission_id, kind, storage_kind, location, mime_type, caption)
                    VALUES(42, 'REFERENCE', 'APP_FILE', 'missing-legacy-reference.png', 'image/png', 'Legacy caption')""")
            }
            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
        }
        legacy.writableDatabase
        legacy.close()
        helper = ObraDatabaseHelper(context, name)
        repository = SqliteStudioRepository(helper, context)
        val snapshot = repository.snapshot()
        assertEquals(2, helper.readableDatabase.version)
        assertEquals(42L, snapshot.commissions().single().id)
        assertEquals(50000L, snapshot.payments().single().amountMinor)
        assertFalse(snapshot.commissions().single().background)
        assertEquals(9L, snapshot.commissions().single().genreId)
        assertEquals("Legacy style", snapshot.commissions().single().tags)
        assertTrue(snapshot.commissions().single().tasks.single().done)
        assertEquals(1, snapshot.commissions().single().images.size)
        helper.readableDatabase.rawQuery("SELECT caption FROM attachments WHERE commission_id = 42", null)
            .use { it.moveToFirst(); assertEquals("Legacy caption", it.getString(0)) }
        helper.readableDatabase.rawQuery("PRAGMA foreign_key_check", null).use { assertFalse(it.moveToFirst()) }
        helper.readableDatabase.rawQuery("PRAGMA integrity_check", null).use { it.moveToFirst(); assertEquals("ok", it.getString(0)) }
    }
}
