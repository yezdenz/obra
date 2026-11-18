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
import com.google.android.material.button.MaterialButton
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
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Exercises the simplified workflow against the runner's isolated database. */
@RunWith(AndroidJUnit4::class)
class EnhancementWorkflowTest {
    private lateinit var context: Context
    private lateinit var app: ObraApplication
    private lateinit var helper: ObraDatabaseHelper
    private val today get() = LocalDate.now()

    @Before fun setup() {
        context = ApplicationProvider.getApplicationContext()
        check(context is BackendTestApplication)
        app = context as ObraApplication
        helper = ObraDatabaseHelper(context)
        clearRows()
    }

    @After fun cleanup() { clearRows(); helper.close() }

    private fun <T> execute(operation: (SqliteStudioRepository) -> T): T {
        val done = CountDownLatch(1)
        var result: Result<T>? = null
        app.execute(operation) { result = it; done.countDown() }
        assertTrue(done.await(15, TimeUnit.SECONDS))
        return requireNotNull(result).getOrThrow()
    }

    private fun clearRows() = execute {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            listOf("artwork_updates", "payments", "attachments", "checklist_items", "commission_tags", "commissions", "clients", "genres")
                .forEach { db.delete(it, null, null) }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    private fun seed() = execute {
        it.saveCommission(CommissionDraft(username = "enhancement.example", title = "Simple workflow artwork",
            genre = "Fantasy", total = "2000.00", commissionOn = today.minusDays(3).toString(),
            dueOn = today.plusDays(1).toString()), UUID.randomUUID().toString())
    }

    @Test fun homeShowsWorkRatherThanDuplicatedStageTasks() {
        val id = seed()
        execute { it.startCommission(id, today.minusDays(2).toString()) }
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { screen ->
            awaitView(screen, "+ Add commission")
            screen.onActivity {
                val all = views(it.window.decorView)
                assertTrue(all.filterIsInstance<CheckBox>().isEmpty())
                assertFalse(all.filterIsInstance<TextView>().any { v -> v.text.contains("Finish lineart") })
                assertFalse(all.filterIsInstance<TextView>().any { v -> v.text.contains("Unfinished tasks") })
                assertTrue(all.filterIsInstance<TextView>().any { v -> v.text.contains("Due tomorrow") })
                assertEquals("+ Add commission", views(it.footer).filterIsInstance<MaterialButton>().single().text.toString())
            }
        }
    }

    @Test fun optionalChecklistStaysHiddenUntilOpenedAndSurvivesRecreation() {
        val id = seed()
        ActivityScenario.launch<DetailActivity>(Intent(context, DetailActivity::class.java)
            .putExtra("commissionId", id)).use { screen ->
            awaitView(screen, "Start commission")
            screen.onActivity {
                assertTrue(views(it.window.decorView).filterIsInstance<CheckBox>().isEmpty())
                click(it, "More details & optional checklist")
            }
            screen.recreate()
            awaitView(screen, "Hide extra details")
            screen.onActivity {
                assertEquals(2, views(it.window.decorView).filterIsInstance<CheckBox>().size)
                views(it.window.decorView).filterIsInstance<CheckBox>().first().performClick()
            }
            awaitCondition("saved optional checklist") { execute { it.snapshot() }.commissions().single().tasks.first().done }
            val saved = execute { it.snapshot() }
            assertEquals("Queued", saved.commissions().single().status)
            assertTrue(saved.payments().isEmpty())
        }
    }

    @Test fun detailsOpensCompletionDirectlyAndKeepsPaymentsIndependent() {
        val id = seed()
        execute {
            it.startCommission(id, today.minusDays(2).toString())
            it.recordPayment(id, 50000, today.toString(), "Cash", "Fictional deposit", UUID.randomUUID().toString())
        }
        val instrumentation = InstrumentationRegistry.getInstrumentation()
            ActivityScenario.launch<DetailActivity>(Intent(context, DetailActivity::class.java)
                .putExtra("commissionId", id)).use { screen ->
                awaitView(screen, "Mark completed")
                screen.onActivity {
                    assertEquals("Mark completed", views(it.footer).filterIsInstance<MaterialButton>().single().text.toString())
                    assertTrue(views(it.window.decorView).filterIsInstance<CheckBox>().isEmpty())
                    click(it, "Mark completed")
                }
                awaitCondition("small completion confirmation") {
                    instrumentation.uiAutomation.rootInActiveWindow?.findAccessibilityNodeInfosByText("Finish this artwork?")?.isNotEmpty()==true
                }
                assertEquals("In progress", execute { it.snapshot() }.commissions().single().status)
                val confirm=instrumentation.uiAutomation.rootInActiveWindow.findAccessibilityNodeInfosByText("Mark completed")
                    .single { it.text?.toString()=="Mark completed" && it.isClickable }
                assertTrue(confirm.performAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK))
                awaitCondition("completed artwork") { execute { it.snapshot() }.commissions().single().status=="Completed" }
                awaitView(screen, "Record payment")
                val saved = execute { it.snapshot() }
                val artwork = saved.commissions().single()
                assertEquals("Completed", artwork.status)
                assertEquals(today.toString(), artwork.finishedOn)
                assertEquals(50000L, saved.payments().single().amountMinor)
                assertEquals(150000L, artwork.totalMinor - saved.payments().sumOf { it.amountMinor })
                assertFalse(artwork.tasks.any { it.done })
            }
    }

    @Test fun keepWorkingCancelsDirectCompletionWithoutSaving() {
        val id = seed()
        execute { it.startCommission(id, today.minusDays(2).toString()) }
        ActivityScenario.launch<DetailActivity>(Intent(context, DetailActivity::class.java)
            .putExtra("commissionId", id)).use { screen ->
            awaitView(screen, "Mark completed")
            screen.onActivity { click(it,"Mark completed") }
            val instrumentation=InstrumentationRegistry.getInstrumentation()
            awaitCondition("completion dialog") {
                instrumentation.uiAutomation.rootInActiveWindow?.findAccessibilityNodeInfosByText("Keep working")?.isNotEmpty()==true
            }
            assertTrue(instrumentation.uiAutomation.rootInActiveWindow.findAccessibilityNodeInfosByText("Keep working")
                .first { it.isClickable }.performAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK))
            screen.recreate()
            awaitView(screen, "Mark completed")
            val saved = execute { it.snapshot() }
            assertEquals("In progress", saved.commissions().single().status)
            assertEquals("Sketch", saved.commissions().single().stage)
            assertTrue(saved.payments().isEmpty())
        }
    }
}
