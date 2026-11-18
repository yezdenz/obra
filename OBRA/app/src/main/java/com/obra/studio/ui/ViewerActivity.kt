package com.obra.studio.ui

import android.os.Bundle
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.LinearLayout
import android.view.ScaleGestureDetector
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
        note(listOfNotNull(commission?.title,"$label ${index + 1} of ${images.size}").joinToString(" · "))
        image = ImageView(this).apply {
            contentDescription = if(label=="Artwork")"Artist artwork image" else "Commission reference image"
            scaleType = ImageView.ScaleType.FIT_CENTER
            background = shape(0xffede5dc.toInt())
        }
        body.addView(image, LinearLayout.LayoutParams(-1, dp(380)))
        if (images.isEmpty()) {
            empty("No reference images", "Attach a reference using Edit commission.")
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
                    result.fold(onSuccess = { image.setImageBitmap(it) }, onFailure = {
                        empty("Image unavailable", "The image may have moved or access may have expired. Your commission is still saved.")
                        button("Retry image") { render() }
                    })
                }
            }
        }
        val scale = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                zoom = (zoom * detector.scaleFactor).coerceIn(1f, 4f)
                image.scaleX = zoom
                image.scaleY = zoom
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
        button("Zoom in") { zoom = (zoom + .5f).coerceAtMost(4f); image.scaleX = zoom; image.scaleY = zoom }
        button("Fit image") { zoom = 1f; image.scaleX = 1f; image.scaleY = 1f; image.translationX = 0f; image.translationY = 0f }
        button("Previous image") { index--; render() }.isEnabled = index > 0
        button("Next image") { index++; render() }.isEnabled = index < images.lastIndex
    }
}
