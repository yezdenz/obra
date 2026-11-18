package com.obra.studio.data.local

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.widget.ImageView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.obra.studio.data.CommissionDraft
import com.obra.studio.ui.ViewerActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ImagePersistenceTest {
    @Test fun contentReferencePersistsRoundTripsAndHandlesRevocation() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val authority = "com.obra.studio.backendtests.documents"
        val fixture = "reference-${UUID.randomUUID()}.png"
        val uri = DocumentsContract.buildDocumentUri(authority, fixture)
        val name = "image-test-${UUID.randomUUID()}.db"
        var helper = ObraDatabaseHelper(context, name)
        context.contentResolver.call(authority, "createFixture", fixture, Bundle().apply { putInt("size", 4096) })
        context.contentResolver.call(authority, "grantFixture", context.packageName,
            Bundle().apply { putString("uri", uri.toString()) })
        try {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            var repository = SqliteStudioRepository(helper, context)
            val id = repository.saveCommission(CommissionDraft(username = "image.client", title = "Image artwork",
                total = "2000.00", images = arrayListOf(uri.toString())), UUID.randomUUID().toString())
            helper.close()
            helper = ObraDatabaseHelper(context, name)
            repository = SqliteStudioRepository(helper, context)
            val original = repository.snapshot().commissions().single()
            assertEquals(uri.toString(), original.images.single())
            repository.updateCommission(id, CommissionDraft(username = original.username, title = "Updated image artwork",
                type = original.type, total = "2000.00", commissionOn = original.commissionOn,
                dueOn = original.dueOn, images = ArrayList(original.images), expectedRevision = original.revision))
            val intent = Intent(context, ViewerActivity::class.java).putStringArrayListExtra("images", arrayListOf(uri.toString()))
            ActivityScenario.launch<ViewerActivity>(intent).use { scenario ->
                awaitCondition("bounded large image") {
                    var decoded = false
                    scenario.onActivity { activity ->
                        val bitmap = (views(activity.window.decorView).filterIsInstance<ImageView>().single().drawable as? BitmapDrawable)?.bitmap
                        decoded = bitmap != null && bitmap.width <= 2048 && bitmap.height <= 2048
                    }
                    decoded
                }
                scenario.recreate()
                awaitView(scenario, "Fit image")
            }
            context.contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.contentResolver.call(authority, "removeFixture", fixture, null)
            val saved = repository.snapshot().commissions().single()
            assertEquals("Updated image artwork", saved.title)
            ActivityScenario.launch<ViewerActivity>(intent).use { scenario ->
                awaitView(scenario, "Image unavailable")
                awaitView(scenario, "Retry image")
            }
            try {
                repository.updateCommission(id, CommissionDraft(username = saved.username, title = saved.title,
                    type = saved.type, total = "2000.00", commissionOn = saved.commissionOn, dueOn = saved.dueOn,
                    images = ArrayList(saved.images), expectedRevision = saved.revision))
                fail("Missing access must retain the existing saved request")
            } catch (_: IllegalArgumentException) { }
            assertEquals(saved.revision, repository.snapshot().commissions().single().revision)
        } finally {
            runCatching { context.contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            context.contentResolver.call(authority, "removeFixture", fixture, null)
            helper.close()
            context.deleteDatabase(name)
        }
    }
}
