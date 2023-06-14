package com.stylit.ui.home

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.graphics.drawable.toBitmap
import com.stylit.R
import java.io.OutputStream
import java.util.UUID

class FullScreenImageHome : ComponentActivity() {
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
        val imageViewFullScreen = findViewById<ImageView>(R.id.image_view_full)
        val byteArray = intent.getByteArrayExtra("imageBytes")
        if (byteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            imageViewFullScreen.setImageBitmap(bitmap)
        }

        sendButton = findViewById(R.id.buttonFullScreenSave)
        sendButton.setOnClickListener {

            // Convert ImageView to Bitmap
            val bitmap = imageViewFullScreen.drawable.toBitmap()

            // Save the Bitmap
            saveImageToGallery(bitmap)

            finish();
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap?) {
        val resolver = contentResolver
        val imageCollection =
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${UUID.randomUUID()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        val uri = resolver.insert(imageCollection, contentValues)
        try {
            val outputStream: OutputStream? = uri?.let { resolver.openOutputStream(it) }
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream?.close()
            Toast.makeText(
                this,
                "Image saved successfully",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Failed to save image: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }
}