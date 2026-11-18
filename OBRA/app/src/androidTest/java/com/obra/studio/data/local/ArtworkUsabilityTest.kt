package com.obra.studio.data.local

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.obra.studio.ObraApplication
import com.obra.studio.data.CommissionDraft
import com.obra.studio.ui.ArtworkActivity
import com.obra.studio.ui.DetailActivity
import com.obra.studio.ui.FormActivity
import com.obra.studio.ui.MainActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ArtworkUsabilityTest {
    private lateinit var context: Context
    private lateinit var app: ObraApplication
    private lateinit var helper: ObraDatabaseHelper
    private val files=mutableListOf<File>()
    private val today get()=LocalDate.now()
    private val instrumentation get()=InstrumentationRegistry.getInstrumentation()

    @Before fun setup() {
        context=ApplicationProvider.getApplicationContext();check(context is BackendTestApplication)
        app=context as ObraApplication;helper=ObraDatabaseHelper(context);clear()
    }
    @After fun cleanup() { clear();helper.close();files.forEach { it.delete() } }
    private fun <T> execute(operation:(SqliteStudioRepository)->T):T {
        val done=CountDownLatch(1);var result:Result<T>?=null
        app.execute(operation) { result=it;done.countDown() }
        assertTrue(done.await(15,TimeUnit.SECONDS));return requireNotNull(result).getOrThrow()
    }
    private fun clear()=execute {
        val db=helper.writableDatabase;db.beginTransaction()
        try {
            listOf("artwork_updates","payments","attachments","checklist_items","commission_tags","commissions","clients","genres")
                .forEach { db.delete(it,null,null) }
            db.execSQL("UPDATE commission_types SET is_active=1")
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }
    private fun seed()=execute { it.saveCommission(CommissionDraft(username="artwork.test",title="Portrait update",
        total="2000.00",commissionOn=today.minusDays(3).toString(),dueOn=today.plusDays(14).toString()),UUID.randomUUID().toString()) }
    private fun image(color:Int):String {
        val file=File(context.filesDir,"artwork-test-${UUID.randomUUID()}.png");files.add(file)
        val bitmap=Bitmap.createBitmap(800,1000,Bitmap.Config.ARGB_8888);bitmap.eraseColor(color)
        file.outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG,100,it)) };bitmap.recycle()
        return android.net.Uri.fromFile(file.canonicalFile).toString()
    }
    private fun capture(name:String) {
        val dir=requireNotNull(context.getExternalFilesDir("enhancement-review")).apply { mkdirs() }
        instrumentation.uiAutomation.takeScreenshot()?.let { bitmap ->
            File(dir,"$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) };bitmap.recycle()
        }
    }

    @Test fun versionsPersistNewestFirstAndFinishedArtworkLeadsHistory() {
        val id=seed();val sketch=image(Color.BLUE);val finished=image(Color.MAGENTA)
        execute { it.startCommission(id,today.minusDays(2).toString()) }
        val token=UUID.randomUUID().toString()
        val first=execute { it.addArtwork(id,"Sketch",sketch,token) }
        assertEquals(first,execute { it.addArtwork(id,"Sketch",sketch,token) })
        execute { it.addArtwork(id,"Finished",finished,UUID.randomUUID().toString()) }
        val last=execute { it.addArtwork(id,"Lineart",sketch,UUID.randomUUID().toString()) }
        helper.close();helper=ObraDatabaseHelper(context)
        val saved=SqliteStudioRepository(helper,context).snapshot().commissions().single()
        assertEquals(3,saved.artworkUpdates.size);assertEquals(last,saved.latestArtwork!!.id)
        assertEquals(sketch,saved.previewImage);assertEquals(finished,saved.historyImage)
        assertEquals("In progress",saved.status);assertEquals("Sketch",saved.stage)
        assertTrue(saved.images.isEmpty());assertTrue(execute { it.snapshot() }.payments().isEmpty())
        execute { it.updateCommission(id,CommissionDraft(username=saved.username,title="Edited request",
            type=saved.type,total="2000.00",commissionOn=saved.commissionOn,dueOn=saved.dueOn,
            images=ArrayList(saved.images),expectedRevision=saved.revision)) }
        assertEquals(last,execute { it.snapshot() }.commissions().single().latestArtwork!!.id)
        try { execute { it.addArtwork(id,"Colors",sketch,token) };fail("Conflicting retry must fail") }
        catch(_:IllegalArgumentException) { }
        try { execute { it.addArtwork(id,"Sketch","content://missing/image",UUID.randomUUID().toString()) };fail("Missing grant must fail") }
        catch(_:IllegalArgumentException) { }
        assertEquals(3,execute { it.snapshot() }.commissions().single().artworkUpdates.size)
    }

    @Test fun versionTwoUpgradeKeepsExistingMoneyAndWork() {
        val name="upgrade-artwork-${UUID.randomUUID()}.db"
        var old=ObraDatabaseHelper(context,name)
        try {
            val repo=SqliteStudioRepository(old,context)
            val id=repo.saveCommission(CommissionDraft(username="old.client",title="Existing work",total="1000.00"),UUID.randomUUID().toString())
            repo.recordPayment(id,25000,today.toString(),"Cash","Old receipt",UUID.randomUUID().toString())
            // All remaining tables/columns exactly match v2 once the new v3 table is removed.
            old.writableDatabase.execSQL("DROP TABLE artwork_updates");old.writableDatabase.version=2;old.close()
            old=ObraDatabaseHelper(context,name)
            val saved=SqliteStudioRepository(old,context).snapshot()
            assertEquals(3,old.readableDatabase.version);assertEquals(id,saved.commissions().single().id)
            assertEquals(25000L,saved.payments().single().amountMinor)
            assertEquals(2,saved.commissions().single().tasks.size)
            assertTrue(saved.commissions().single().artworkUpdates.isEmpty())
            old.readableDatabase.rawQuery("PRAGMA foreign_key_check",null).use { assertFalse(it.moveToFirst()) }
        } finally { old.close();context.deleteDatabase(name) }
    }

    @Test fun datesUseReadableFieldsAndKeepTheDraftWhenCalendarRecreates() {
        ActivityScenario.launch<FormActivity>(Intent(context,FormActivity::class.java)).use { screen ->
            awaitView(screen,"Next: Artwork")
            screen.onActivity {
                field(it,"Client username *").setText("dates.client");click(it,"Next: Artwork")
                field(it,"Project title *").setText("Date field check");click(it,"Next: Price & dates")
                field(it,"Agreed total (PHP) *").setText("1734.56")
                assertFalse(views(it.window.decorView).filterIsInstance<CheckBox>().any { box -> box.text.contains("background",true) })
                assertFalse(views(it.window.decorView).filterIsInstance<TextView>().any { v -> v.text.contains("Suggested quote") })
                val date=views(it.window.decorView).single { v -> v.tag=="date-field:Due date" }
                assertFalse(date.contentDescription.toString().contains(today.plusDays(14).toString()))
            }
            instrumentation.waitForIdleSync();capture("date-fields")
            screen.onActivity { views(it.window.decorView).single { v -> v.tag=="date-field:Due date" }.performClick() }
            awaitCondition("calendar shown") { instrumentation.uiAutomation.rootInActiveWindow?.findAccessibilityNodeInfosByText("Use date")?.isNotEmpty()==true }
            capture("calendar")
            screen.recreate()
            awaitView(screen,"Review commission")
            val use=instrumentation.uiAutomation.rootInActiveWindow.findAccessibilityNodeInfosByText("Use date")
                .first { it.isClickable }
            assertTrue(use.performAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK))
            awaitCondition("calendar dismissed") { instrumentation.uiAutomation.rootInActiveWindow?.findAccessibilityNodeInfosByText("Use date")?.isEmpty()==true }
            screen.onActivity { assertEquals("1734.56",field(it,"Agreed total (PHP) *").text.toString()) }
        }
    }

    @Test fun selectedArtworkSurvivesRecreationAndSavesUsingThePicker() {
        val id=seed();val authority="com.obra.studio.backendtests.documents"
        val fixture="artwork-${UUID.randomUUID()}.png"
        val uri=DocumentsContract.buildDocumentUri(authority,fixture)
        context.contentResolver.call(authority,"createFixture",fixture,Bundle().apply { putInt("size",4096) })
        context.contentResolver.call(authority,"grantFixture",context.packageName,Bundle().apply { putString("uri",uri.toString()) })
        val result=Intent().setData(uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        var monitor: Instrumentation.ActivityMonitor?=null
        try {
            ActivityScenario.launch<ArtworkActivity>(Intent(context,ArtworkActivity::class.java).putExtra("commissionId",id)).use { screen ->
                awaitView(screen,"Choose image")
                val filter=IntentFilter(Intent.ACTION_OPEN_DOCUMENT).apply { addDataType("*/*");addCategory(Intent.CATEGORY_OPENABLE) }
                monitor=instrumentation.addMonitor(filter,Instrumentation.ActivityResult(Activity.RESULT_OK,result),true)
                screen.onActivity { click(it,"Choose image") }
                awaitView(screen,"Save artwork")
                instrumentation.removeMonitor(monitor);monitor=null
                screen.recreate();awaitView(screen,"Save artwork")
                awaitCondition("decoded upload preview") {
                    var ready=false
                    screen.onActivity {
                        val bitmap=(views(it.window.decorView).filterIsInstance<ImageView>()
                            .firstOrNull { v -> v.contentDescription=="Selected artwork" }?.drawable as? BitmapDrawable)?.bitmap
                        ready=bitmap!=null && bitmap.width<=480 && bitmap.height<=480
                    };ready
                }
                capture("artwork-upload")
                screen.onActivity { click(it,"Save artwork") }
                awaitCondition("artwork saved") { screen.state==Lifecycle.State.DESTROYED }
                assertEquals(uri.toString(),execute { it.snapshot() }.commissions().single().previewImage)
            }
        } finally {
            monitor?.let { instrumentation.removeMonitor(it) }
            runCatching { context.contentResolver.releasePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            context.contentResolver.call(authority,"removeFixture",fixture,null)
        }
    }

    @Test fun workAndCompactHistoryRenderTheirCorrectArtwork() {
        val id=seed();val sketch=image(Color.BLUE);val final=image(Color.MAGENTA)
        execute {
            it.startCommission(id,today.minusDays(2).toString())
            it.addArtwork(id,"Finished",final,UUID.randomUUID().toString())
            it.addArtwork(id,"Lineart",sketch,UUID.randomUUID().toString())
            it.recordPayment(id,50000,today.toString(),"GCash","Deposit",UUID.randomUUID().toString())
            it.recordPayment(id,120000,today.toString(),"Cash","Balance payment",UUID.randomUUID().toString())
        }
        ActivityScenario.launch<MainActivity>(Intent(context,MainActivity::class.java)).use { screen ->
            awaitView(screen,"+ Add commission")
            awaitCondition("newest work thumbnail") {
                var correct=false
                screen.onActivity {
                    val bmp=(views(it.window.decorView).filterIsInstance<ImageView>().firstOrNull { v -> v.contentDescription=="Latest artwork for Portrait update" }?.drawable as? BitmapDrawable)?.bitmap
                    correct=bmp!=null && bmp.getPixel(0,0)==Color.BLUE && bmp.width<=480 && bmp.height<=480
                };correct
            }
            capture("home-artwork")
            screen.onActivity { click(it,"History") };awaitView(screen,"2 payments received")
            awaitCondition("finished images on receipt rows") {
                var correct=false
                screen.onActivity {
                    val images=views(it.window.decorView).filterIsInstance<ImageView>().filter { v -> v.contentDescription=="Portrait update artwork" }
                    correct=images.size==2 && images.all { v -> (v.drawable as? BitmapDrawable)?.bitmap?.getPixel(0,0)==Color.MAGENTA }
                };correct
            }
            capture("compact-history")
        }
    }

    @Test fun completedArtworkHasAnExplicitRouteBackHome() {
        val id=seed()
        execute { it.startCommission(id,today.minusDays(2).toString());it.updateProgress(id,"Finished",today.toString()) }
        ActivityScenario.launch<DetailActivity>(Intent(context,DetailActivity::class.java).putExtra("commissionId",id)).use { screen ->
            awaitView(screen,"Back to Home")
            val monitor=instrumentation.addMonitor(MainActivity::class.java.name,null,false)
            try {
                screen.onActivity { click(it,"Back to Home") }
                val home=instrumentation.waitForMonitorWithTimeout(monitor,10000)
                assertNotNull(home)
                awaitCondition("returned Home") {
                    var ready=false
                    instrumentation.runOnMainSync {
                        ready=views(home!!.window.decorView).filterIsInstance<TextView>().any { it.text.toString()=="+ Add commission" && it.isEnabled }
                    };ready
                }
                assertEquals("Completed",execute { it.snapshot() }.commissions().single().status)
                instrumentation.runOnMainSync { home?.finish() }
            } finally { instrumentation.removeMonitor(monitor) }
        }
    }
}
