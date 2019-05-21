package com.natallia.radaman.jokesTextViewResizable

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ShareViewHelper {
    private val LOG_TAG = ShareViewHelper::class.java.simpleName

    fun share(context: Context, container: View) {
        val screenshot = getBitmapFromView(container)
        val uri = saveImage(context, screenshot)
        uri?.let { shareImageUri(context, it) }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    private fun shareImageUri(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/png"
        }
        context.startActivity(intent)
    }

    private fun saveImage(context: Context, image: Bitmap): Uri? {
        val imagesFolder = File(context.cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_image.png")

            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException while trying to write file for sharing: " + e.message)
        }

        return uri
    }
}
