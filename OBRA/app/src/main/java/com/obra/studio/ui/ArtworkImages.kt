package com.obra.studio.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.widget.ImageView
import java.util.concurrent.Executors
import java.util.concurrent.Future

/** Bounded, off-main-thread decoding for artwork cards, receipt thumbnails, and upload previews. */
class ArtworkImages(private val context: Context) {
    private val worker = Executors.newFixedThreadPool(2)
    private val main = Handler(Looper.getMainLooper())
    private val jobs = mutableListOf<Future<*>>()
    private var generation = 0
    private val cache = object : LruCache<String, Bitmap>(4 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }

    fun load(view: ImageView, location: String, edge: Int = 480) {
        val expected = generation
        val key = "$edge:$location"
        cache.get(key)?.let { showImage(view,it);return }
        jobs.add(worker.submit {
            val result = runCatching {
                val uri = Uri.parse(location)
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
                require(bounds.outWidth > 0 && bounds.outHeight > 0)
                var sample = 1
                while (bounds.outWidth / sample > edge || bounds.outHeight / sample > edge) sample *= 2
                val options = BitmapFactory.Options().apply { inSampleSize=sample }
                requireNotNull(context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it,null,options) })
            }
            main.post {
                if (expected != generation) { result.getOrNull()?.recycle(); return@post }
                result.onSuccess { bitmap ->
                    cache.put(key,bitmap);showImage(view,bitmap)
                }.onFailure { view.contentDescription = "Artwork image unavailable" }
            }
        })
    }

    private fun showImage(view: ImageView, bitmap: Bitmap) {
        view.imageTintList=null;view.setPadding(0,0,0,0)
        view.scaleType=ImageView.ScaleType.CENTER_CROP
        view.setImageBitmap(bitmap)
    }

    fun clear() { generation++;jobs.forEach { it.cancel(true) };jobs.clear() }
    fun close() { clear();worker.shutdownNow();cache.evictAll() }
}
