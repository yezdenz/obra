package com.obra.studio.ui

import android.os.Bundle
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.view.ScaleGestureDetector
import com.obra.studio.R
import com.obra.studio.data.Studio
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ViewerActivity : BaseActivity() {
    override val usesStudioData = false
    private var index = 0
    private var zoom = 1f
    private var images = arrayListOf<String>()
    private lateinit var image: ImageView
    private val imageWorker = Executors.newSingleThreadExecutor()
    private var imageLoad: Future<*>? = null
    private var imageRequest = 0

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        images = intent.getStringArrayListExtra("images") ?: arrayListOf()
        index = (state?.getInt("index") ?: intent.getIntExtra("index", 0))
            .coerceIn(0, images.lastIndex.coerceAtLeast(0))
        render()
    }
    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)
        out.putInt("index", index)
    }
    override fun onDestroy() {
        imageRequest++
        imageLoad?.cancel(true)
        imageWorker.shutdownNow()
        super.onDestroy()
    }

    private fun decode(uri: Uri): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
        require(bounds.outWidth > 0 && bounds.outHeight > 0) { "Unsupported image" }
        var sample = 1
        while (bounds.outWidth / sample > 2048 || bounds.outHeight / sample > 2048) sample *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        return requireNotNull(contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, options) })
    }

    private fun render() {
        imageLoad?.cancel(true)
        val request = ++imageRequest
        zoom = 1f
        val label=intent.getStringExtra("imageLabel") ?: "Reference"
        reset(if(label=="Artwork")"Your artwork" else "Image preview")
        val commission = Studio.work(intent.getLongExtra("commissionId", 0))
        note(listOfNotNull(commission?.title,"$label ${if(images.isEmpty())0 else index + 1} of ${images.size}").joinToString(" · "))
        image = ImageView(this).apply {
            contentDescription = if(label=="Artwork")"Artist artwork image" else "Commission reference image"
            scaleType = ImageView.ScaleType.FIT_CENTER
            background = shape(imageTone)
        }
        body.addView(image, LinearLayout.LayoutParams(-1, dp(380)))
        note("Pinch to zoom · Drag to pan when enlarged")
        val toolbar=LinearLayout(this).apply { gravity=android.view.Gravity.CENTER }
        footer.addView(toolbar,LinearLayout.LayoutParams(-1,-2))
        fun control(name: String, icon: Int, action: ()->Unit): ImageButton =
            iconButton(name,icon,toolbar,action).apply {
                layoutParams=LinearLayout.LayoutParams(0,dp(48),1f).apply {
                    marginStart=dp(3);marginEnd=dp(3)
                }
            }
        var imageReady=false
        lateinit var zoomIn: ImageButton
        lateinit var zoomOut: ImageButton
        lateinit var fit: ImageButton
        fun applyZoom(value: Float) {
            zoom=value.coerceIn(1f,4f)
            image.scaleX=zoom;image.scaleY=zoom
            image.translationX=image.translationX.coerceIn(-image.width*(zoom-1)/2,image.width*(zoom-1)/2)
            image.translationY=image.translationY.coerceIn(-image.height*(zoom-1)/2,image.height*(zoom-1)/2)
            zoomOut.isEnabled=imageReady && zoom>1f
            zoomIn.isEnabled=imageReady && zoom<4f
            fit.isEnabled=imageReady
        }
        control("Previous image",R.drawable.ic_previous) { if(index>0) { index--;render() } }
            .isEnabled=index>0
        zoomOut=control("Zoom out",R.drawable.ic_zoom_out) { applyZoom(zoom-.5f) }
        fit=control("Fit image",R.drawable.ic_fit_image) { applyZoom(1f) }
        zoomIn=control("Zoom in",R.drawable.ic_zoom_in) { applyZoom(zoom+.5f) }
        control("Next image",R.drawable.ic_next) { if(index<images.lastIndex) { index++;render() } }
            .isEnabled=index<images.lastIndex
        applyZoom(1f)
        if (images.isEmpty()) {
            empty(if(label=="Artwork")"No artwork images" else "No reference images",
                if(label=="Artwork")"Add artwork from Commission details." else "Attach a reference using Edit commission.")
        } else {
            val loading = text("Loading image…", 14)
            val uri = Uri.parse(images[index])
            imageLoad = imageWorker.submit {
                val result = runCatching { decode(uri) }
                runOnUiThread {
                    if (isDestroyed || isFinishing || request != imageRequest) {
                        result.getOrNull()?.recycle()
                        return@runOnUiThread
                    }
                    body.removeView(loading)
                    result.fold(onSuccess = { image.setImageBitmap(it);imageReady=true;applyZoom(1f) }, onFailure = {
                        empty("Image unavailable", "The image may have moved or access may have expired. Your commission is still saved.")
                        button("Retry image") { render() }
                    })
                }
            }
        }
        val scale = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if(!imageReady)return false
                applyZoom(zoom * detector.scaleFactor)
                return true
            }
        })
        var x = 0f
        var y = 0f
        image.setOnTouchListener { view, event ->
            scale.onTouchEvent(event)
            when (event.actionMasked) {
                android.view.MotionEvent.ACTION_DOWN -> { x = event.rawX; y = event.rawY }
                android.view.MotionEvent.ACTION_MOVE -> if (zoom > 1 && !scale.isInProgress) {
                    view.translationX = (view.translationX + event.rawX - x).coerceIn(-view.width * (zoom - 1) / 2, view.width * (zoom - 1) / 2)
                    view.translationY = (view.translationY + event.rawY - y).coerceIn(-view.height * (zoom - 1) / 2, view.height * (zoom - 1) / 2)
                    x = event.rawX; y = event.rawY
                }
                android.view.MotionEvent.ACTION_UP -> view.performClick()
            }
            true
        }
    }
}
