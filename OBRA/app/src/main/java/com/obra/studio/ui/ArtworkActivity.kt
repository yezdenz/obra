package com.obra.studio.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.obra.studio.data.Studio
import java.util.UUID

/** A saved artist upload, distinct from client-supplied reference images. */
class ArtworkActivity : BaseActivity() {
    private var image: String? = null
    private var stage = ""
    private var token = UUID.randomUUID().toString()
    private var attempted = false
    private var initialized = false
    private val workId get() = intent.getLongExtra("commissionId",0)
    private val picker=registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if(uri!=null) {
            try {
                contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                image=uri.toString();newInput()
            } catch (_: SecurityException) {
                error("This provider could not keep access to the image. Please choose it from another provider.")
            }
            if(initialized)render()
        }
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        image=state?.getString("image");stage=state?.getString("stage").orEmpty()
        token=state?.getString("token") ?: token;attempted=state?.getBoolean("attempted") ?: false
        onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(image!=null)confirm("Discard artwork upload?","The selected image has not been saved."){finish()}
                else finish()
            }
        })
    }

    override fun onSaveInstanceState(out: Bundle) {
        out.putString("image",image);out.putString("stage",stage);out.putString("token",token)
        out.putBoolean("attempted",attempted);super.onSaveInstanceState(out)
    }

    override fun onStudioReady() {
        val c=Studio.work(workId)
        if(c==null) {reset("Artwork unavailable");empty("Record unavailable","Return to your commissions.");return}
        if(attempted && c.artworkUpdates.any { it.submissionToken==token }) {finish();return}
        if(stage.isBlank())stage=if(c.status=="Completed")"Finished" else c.stage.ifBlank { "Sketch" }
        initialized=true;render()
    }

    private fun newInput() { if(attempted) {token=UUID.randomUUID().toString();attempted=false} }
    private fun render() {
        reset("Add artwork")
        val c=Studio.work(workId) ?: return
        section(c.title)
        note("Add your own sketch, lineart, color progress, or finished piece. Newest uploads appear first.")
        choose("Artwork stage",listOf("Sketch","Lineart","Colors","Finished"),stage) {
            if(stage!=it) {stage=it;newInput()}
        }
        image?.let { selected ->
            val preview=thumbnail(selected,"Selected artwork",240)
            body.addView(preview,android.widget.LinearLayout.LayoutParams(-1,dp(240)).apply { topMargin=dp(12);bottomMargin=dp(12) })
            preview.setOnClickListener {
                startActivity(Intent(this,ViewerActivity::class.java).putExtra("imageLabel","Artwork")
                    .putStringArrayListExtra("images",arrayListOf(selected)))
            }
        }
        button(if(image==null)"Choose image" else "Choose another image") { picker.launch(arrayOf("image/*")) }
        note("This adds an image only. It does not change work status or record a payment.")
        button("Save artwork",footer,true) {
            val selected=requireNotNull(image)
            attempted=true
            write({it.addArtwork(workId,stage,selected,token)}){finish()}
        }.isEnabled=image!=null
    }
}
