package com.obra.studio.data.local

import android.content.Context
import android.content.Intent
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.obra.studio.ObraApplication
import com.obra.studio.data.CommissionDraft
import com.obra.studio.ui.DetailActivity
import com.obra.studio.ui.FormActivity
import com.obra.studio.ui.MainActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class BackendRegressionTest {
    private lateinit var context: Context
    private lateinit var app: ObraApplication
    private lateinit var helper: ObraDatabaseHelper
    @Before fun setup() {
        context = ApplicationProvider.getApplicationContext()
        check(context is BackendTestApplication)
        app = context as ObraApplication
        helper = ObraDatabaseHelper(context)
        clearTestRows()
    }
    @After fun cleanup() { clearTestRows(); helper.close() }

    private fun clearTestRows() = execute {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            listOf("payments", "attachments", "checklist_items", "commission_tags", "commissions", "clients", "genres")
                .forEach { db.delete(it, null, null) }
            db.execSQL("UPDATE commission_types SET is_active = 1")
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }
    private fun <T> execute(operation: (SqliteStudioRepository) -> T): T {
        val done = CountDownLatch(1)
        var result: Result<T>? = null
        app.execute(operation) { result = it; done.countDown() }
        assertTrue(done.await(15, TimeUnit.SECONDS))
        return requireNotNull(result).getOrThrow()
    }
    private fun seed(username: String = "regression.client") = execute {
        it.saveCommission(CommissionDraft(username = username, title = "Regression artwork", total = "2000.00",
            genre = "Fantasy", commissionOn = "2026-10-01", dueOn = "2026-11-10"), UUID.randomUUID().toString())
    }
    private fun form(mode: String, id: Long) = ActivityScenario.launch<FormActivity>(
        Intent(context, FormActivity::class.java).putExtra("mode", mode).putExtra("commissionId", id))
    private fun <A : android.app.Activity> spinner(scenario: ActivityScenario<A>, index: Int) {
        scenario.onActivity { views(it.window.decorView).filterIsInstance<Spinner>().single().setSelection(index) }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun <A : android.app.Activity> returnFromViewer(scenario: ActivityScenario<A>) {
        val instrumentation=InstrumentationRegistry.getInstrumentation()
        val monitor=instrumentation.addMonitor("com.obra.studio.ui.ViewerActivity",null,false)
        try {
            scenario.onActivity { it.startActivity(Intent(it,com.obra.studio.ui.ViewerActivity::class.java)) }
            val viewer=instrumentation.waitForMonitorWithTimeout(monitor,10000)
            assertNotNull(viewer)
            instrumentation.runOnMainSync { viewer?.finish() }
            instrumentation.waitForIdleSync()
        } finally { instrumentation.removeMonitor(monitor) }
    }

    @Test fun loadedScreenCanRetryAReadFailure() {
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
            awaitView(scenario, "+ Add commission")
            execute { helper.writableDatabase.execSQL("ALTER TABLE commission_tags RENAME TO unavailable_tags") }
            try {
                returnFromViewer(scenario)
                awaitView(scenario, "Retry refresh")
            } finally {
                execute { helper.writableDatabase.execSQL("ALTER TABLE unavailable_tags RENAME TO commission_tags") }
            }
            scenario.onActivity { click(it, "Retry refresh") }
            awaitView(scenario, "+ Add commission")
            scenario.onActivity {
                assertFalse(views(it.window.decorView).filterIsInstance<TextView>().any { it.text == "Retry refresh" })
            }
        }
    }

    @Test fun committedTaskSurvivesFailedRefreshAndRetry() {
        val id = seed()
        ActivityScenario.launch<DetailActivity>(Intent(context, DetailActivity::class.java).putExtra("commissionId", id)).use { scenario ->
            awaitView(scenario, "Start commission")
            execute { helper.writableDatabase.execSQL("ALTER TABLE commission_tags RENAME TO unavailable_tags") }
            try {
                scenario.onActivity { views(it.window.decorView).filterIsInstance<CheckBox>().first().performClick() }
                awaitView(scenario, "Saved successfully")
                awaitView(scenario, "Retry refresh")
            } finally {
                execute { helper.writableDatabase.execSQL("ALTER TABLE unavailable_tags RENAME TO commission_tags") }
            }
            scenario.onActivity { click(it, "Retry refresh") }
            awaitCondition("refreshed task state") {
                var checked = false
                scenario.onActivity { checked = views(it.window.decorView).filterIsInstance<CheckBox>().first().isChecked }
                checked
            }
            assertEquals(0, execute { it.snapshot() }.payments().size)
        }
    }

    @Test fun reconnectRequiresAChoiceAfterItsSourceFinishes() {
        val first = seed()
        val second = seed("another.client")
        execute { it.startCommission(first, "2026-10-02"); it.startCommission(second, "2026-10-02") }
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
            awaitView(scenario, "+ Add commission")
            scenario.onActivity { click(it, "Reconnect") }
            spinner(scenario, 1)
            execute { it.updateProgress(first, "Finished", "2026-10-03") }
            returnFromViewer(scenario)
            awaitView(scenario, "Previous selection is no longer current")
            scenario.recreate()
            awaitView(scenario, "Previous selection is no longer current")
            spinner(scenario, 1)
            awaitView(scenario, "View previous commission")
        }
    }

    @Test fun reportScrollRestoresAfterRecreation() {
        repeat(12) { index ->
            val id = seed("client.$index")
            execute { it.recordPayment(id, 10000, java.time.LocalDate.now().toString(), "GCash", "Test receipt", UUID.randomUUID().toString()) }
        }
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
            awaitView(scenario, "+ Add commission")
            scenario.onActivity { click(it, "History") }
            awaitView(scenario, "12 payments received")
            scenario.onActivity { it.scroll.scrollTo(0, 500) }
            scenario.recreate()
            awaitView(scenario, "12 payments received")
            awaitCondition("restored report scroll") {
                var restored = false
                scenario.onActivity { restored = it.scroll.scrollY == 500 }
                restored
            }
        }
    }

    @Test fun methodOnlyChangeRequiresDiscardConfirmation() {
        val id = seed()
        form("payment", id).use { scenario ->
            awaitView(scenario, "Save payment")
            spinner(scenario, 1)
            scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            assertEquals(Lifecycle.State.RESUMED, scenario.state)
            scenario.onActivity { assertFalse(it.isFinishing) }
            InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        }
    }

    @Test fun paymentAndAddRecoverWhileTheirWritesAreQueued() {
        val id = seed()
        val gate = CountDownLatch(1)
        val payment = form("payment", id)
        try {
            awaitView(payment, "Save payment")
            payment.onActivity { field(it, "Amount received (PHP) *").setText("500.00") }
            app.execute({ check(gate.await(15, TimeUnit.SECONDS)) }) { }
            payment.onActivity { click(it, "Save payment") }
            payment.recreate()
            gate.countDown()
            awaitCondition("restored payment form completion") { payment.state == Lifecycle.State.DESTROYED }
            assertEquals(1, execute { it.snapshot() }.payments().size)
        } finally { gate.countDown(); payment.close() }

        val addGate = CountDownLatch(1)
        val monitor = InstrumentationRegistry.getInstrumentation().addMonitor("com.obra.studio.ui.DetailActivity", null, false)
        ActivityScenario.launch<FormActivity>(Intent(context, FormActivity::class.java)).use { add ->
            try {
                awaitView(add, "Next: Artwork")
                add.onActivity {
                    field(it, "Client username *").setText("queued.save.client")
                    click(it, "Next: Artwork")
                    field(it, "Project title *").setText("Queued Add")
                    click(it, "Next: Price & dates")
                    click(it, "Review commission")
                }
                app.execute({ check(addGate.await(15, TimeUnit.SECONDS)) }) { }
                add.onActivity { click(it, "Save commission") }
                add.recreate()
                addGate.countDown()
                val detail = InstrumentationRegistry.getInstrumentation().waitForMonitorWithTimeout(monitor, 15000)
                assertNotNull(detail)
                assertEquals(1, execute { it.snapshot() }.commissions().count { it.username == "queued.save.client" })
                InstrumentationRegistry.getInstrumentation().runOnMainSync { detail?.finish() }
            } finally { addGate.countDown(); InstrumentationRegistry.getInstrumentation().removeMonitor(monitor) }
        }
    }

    @Test fun editPaymentProgressAndReportsWorkThroughTheirScreens() {
        val id = seed()
        form("edit", id).use { scenario ->
            awaitView(scenario, "Next: Artwork")
            scenario.onActivity {
                click(it, "Next: Artwork")
                field(it, "Project title *").setText("Edited through form")
                click(it, "Next: Price & dates")
                click(it, "Review changes")
                click(it, "Save changes")
            }
            awaitCondition("Edit completion") { scenario.state == Lifecycle.State.DESTROYED }
        }
        execute { it.startCommission(id, "2026-10-02") }
        form("payment", id).use { scenario ->
            awaitView(scenario, "Save payment")
            scenario.onActivity { field(it, "Amount received (PHP) *").setText("500.00"); click(it, "Save payment") }
            awaitCondition("payment completion") { scenario.state == Lifecycle.State.DESTROYED }
        }
        form("progress", id).use { scenario ->
            awaitView(scenario, "Continue")
            spinner(scenario, 2)
            scenario.onActivity { click(it, "Continue") }
            awaitCondition("progress completion") { scenario.state == Lifecycle.State.DESTROYED }
        }
        form("progress", id).use { scenario ->
            awaitView(scenario, "Continue")
            spinner(scenario, 3)
            scenario.onActivity { click(it, "Continue"); click(it, "Mark completed") }
            awaitCondition("completion confirmation") { scenario.state == Lifecycle.State.DESTROYED }
        }
        val saved = execute { it.snapshot() }
        assertEquals("Edited through form", saved.commissions().single().title)
        assertEquals("Completed", saved.commissions().single().status)
        assertEquals(50000L, saved.payments().single().amountMinor)
        assertFalse(saved.commissions().single().tasks.any { it.done })
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
            awaitView(scenario, "+ Add commission")
            scenario.onActivity { click(it, "Insights") }
            awaitView(scenario, "All-time received")
            scenario.onActivity { click(it, "View payments") }
            awaitView(scenario, "1 payments received")
        }
    }
}

