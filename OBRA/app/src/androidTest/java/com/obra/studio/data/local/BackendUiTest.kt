package com.obra.studio.data.local

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.obra.studio.ObraApplication
import com.obra.studio.ui.FormActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Exercises the ordinary four-step form and its navigation to a persisted detail record. */
@RunWith(AndroidJUnit4::class)
class BackendUiTest {
    @Test fun intakeRestoresDraftAndSavesThroughTheScreen() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val app = context as ObraApplication
        val username = "ui-test-${UUID.randomUUID()}"
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val detailMonitor = instrumentation.addMonitor("com.obra.studio.ui.DetailActivity", null, false)
        val scenario = ActivityScenario.launch<FormActivity>(Intent(context, FormActivity::class.java))
        try {
            awaitView(scenario, "Next: Artwork")
            scenario.onActivity { activity ->
                field(activity, "Client username *").setText(username)
                field(activity, "Display name · Optional").setText("UI test client")
                click(activity, "Next: Artwork")
                field(activity, "Project title *").setText("UI integration artwork")
            }
            scenario.recreate()
            awaitView(scenario, "Next: Price & dates")
            scenario.onActivity { activity ->
                assertEquals("UI integration artwork", field(activity, "Project title *").text.toString())
                click(activity, "Next: Price & dates")
                field(activity, "Agreed total (PHP) *").setText("2000.00")
                click(activity, "Review commission")
                click(activity, "Save commission")
            }
            val detail = instrumentation.waitForMonitorWithTimeout(detailMonitor, 10000)
            assertNotNull("Save must navigate to the persisted commission", detail)
            val checked = CountDownLatch(1)
            var failure: Throwable? = null
            app.execute({ it.snapshot() }) { result ->
                try {
                    val saved = result.getOrThrow().commissions().single { it.username == username }
                    assertEquals("Queued", saved.status)
                    assertEquals("UI test client", saved.displayName)
                    assertEquals(200000L, saved.totalMinor)
                    assertEquals(2, saved.tasks.size)
                } catch (error: Throwable) { failure = error }
                finally { checked.countDown() }
            }
            assertTrue(checked.await(10, TimeUnit.SECONDS))
            failure?.let { throw it }
            instrumentation.runOnMainSync { detail?.finish() }
        } finally {
            scenario.close()
            instrumentation.removeMonitor(detailMonitor)
            // Remove only the fictional records created by this test's unique username.
            ObraDatabaseHelper(context).use { helper ->
                val db = helper.writableDatabase
                db.beginTransaction()
                try {
                    val ids = mutableListOf<Long>()
                    db.rawQuery("""SELECT c.id FROM commissions c JOIN clients cl ON cl.id = c.client_id
                        WHERE cl.username = ?""", arrayOf(username)).use { rows ->
                        while (rows.moveToNext()) ids.add(rows.getLong(0))
                    }
                    ids.forEach { id ->
                        listOf("artwork_updates", "payments", "attachments", "checklist_items", "commission_tags").forEach { table ->
                            db.delete(table, "commission_id = ?", arrayOf(id.toString()))
                        }
                        db.delete("commissions", "id = ?", arrayOf(id.toString()))
                    }
                    db.delete("clients", "username = ?", arrayOf(username))
                    db.setTransactionSuccessful()
                } finally { db.endTransaction() }
            }
        }
    }

}
