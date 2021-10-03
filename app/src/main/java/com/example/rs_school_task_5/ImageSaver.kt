package com.example.rs_school_task_5

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

object ImageSaver {

    private var contentResolver: ContentResolver? = null

    fun setContentResolver(contentResolver: ContentResolver) {
        this.contentResolver = contentResolver
    }

    fun createBitmap(imageView: ImageView): Bitmap {
        val bitmapDrawable = imageView.drawable as BitmapDrawable
        return bitmapDrawable.bitmap
    }

    suspend fun saveImageToGallery(
        displayName: String, bitmap: Bitmap, quality: Int = 100, observer: ImageSaverObserver
    ) {
        withContext(Dispatchers.IO) {
            val imageCollection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bitmap.width)
                put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            }

            try {
                contentResolver?.insert(imageCollection, contentValues)?.also { uri ->
                    contentResolver?.openOutputStream(uri).use { outputStream ->
                        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream))
                            throw IOException("Couldn't save bitmap")
                    }
                } ?: throw IOException("Couldn't create MediaStore entry")
                Log.i("SAVING IMAGE", "Image is saved successfully!")
                withContext(Dispatchers.Main) {
                    observer.onSavedSuccessfully()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("SAVING IMAGE", "Failed to save image..")
                withContext(Dispatchers.Main) {
                    observer.onSavingFailure()
                }
            }
        }
    }
}
