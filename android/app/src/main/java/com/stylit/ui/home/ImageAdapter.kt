package com.stylit.ui.home

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.stylit.R
import java.io.OutputStream
import java.util.UUID

class ImageAdapter(private val context: Context, private val recyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: MutableList<Any> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return if (viewType == VIEW_TYPE_IMAGE) {
            val view = inflater.inflate(R.layout.item_image, parent, false)
            val button = view.findViewById<Button>(R.id.saveImageButton)
            button.tag = "button_" + (items.size-1)
            ImageViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_text, parent, false)
            TextViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is ImageViewHolder && item is String) {
            Picasso.get().load(item).into(holder.imageView)

            // Set click listener for the button
            val buttonSaveImage = holder.itemView.findViewById<Button>(R.id.saveImageButton)
            val imageSave = holder.itemView.findViewById<ImageView>(R.id.imageViewSave)
            buttonSaveImage.setOnClickListener {
                val clickedPosition = holder.adapterPosition
                Log.d("MyLogs", clickedPosition.toString())

                // Convert ImageView to Bitmap
                val bitmap = imageSave.drawable.toBitmap()

                // Save the Bitmap
                saveImageToGallery(bitmap)
            }

        } else if (holder is TextViewHolder && item is String) {
            holder.textView.text = item
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun saveImageToGallery(bitmap: Bitmap?) {
        val resolver = context.contentResolver
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
                context,
                "Image saved successfully",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Failed to save image: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return if (item is String && URLUtil.isValidUrl(item) && item.endsWith(".jpeg")) {
            VIEW_TYPE_IMAGE
        } else {
            VIEW_TYPE_TEXT
        }
    }

    fun addImage(imageUrl: String) {
        items.add(imageUrl)
        notifyItemInserted(items.size - 1)

        recyclerView.smoothScrollToPosition(items.size - 1)
    }

    fun addText(textMessage: String) {
        items.add(textMessage)
        notifyItemInserted(items.size - 1)

        recyclerView.smoothScrollToPosition(items.size - 1)
    }


    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewSave)
    }

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    fun getCount(): Int {
        return items.size
    }

    companion object {
        private const val VIEW_TYPE_IMAGE = 1
        private const val VIEW_TYPE_TEXT = 2
    }
}
