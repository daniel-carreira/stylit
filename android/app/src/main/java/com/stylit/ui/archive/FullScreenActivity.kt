package com.stylit.ui.archive

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.stylit.R

class FullScreenActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var linearLayout: LinearLayout
    private var originalForeground: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)

        imageView = findViewById(R.id.image_view)

        supportActionBar?.hide()
        supportActionBar?.title = "Full Screen Image"

        val i: Intent = intent

        val position: Int = i.extras?.getInt("id") ?: 0
        val archiveAdapter = ArchiveAdapter(this)

        imageView.setImageResource(archiveAdapter.imageArray[position])

        linearLayout = findViewById(R.id.linear_layout)
        for (i in 0 until archiveAdapter.imageArray.size) {
            val image = ImageView(this)
            image.setImageResource(archiveAdapter.imageArray[i])
            image.scaleType = ImageView.ScaleType.CENTER_CROP
            val layoutParams = ViewGroup.MarginLayoutParams(300, 300)
            image.layoutParams = layoutParams
            image.isClickable = true // Set clickable to true

            image.setOnClickListener {
                toggleImageBorder(image)
                Log.d("MyLogs", "Image clicked")
            }

            linearLayout.addView(image)
        }
    }

  
    private fun toggleImageBorder(imageView: ImageView) {
        for (i in 0 until linearLayout.childCount) {
            val child = linearLayout.getChildAt(i)
            if (child is ImageView) {
                if (child == imageView) {
                    val borderDrawable = ContextCompat.getDrawable(this, R.drawable.border)
                    child.foreground = borderDrawable
                } else {
                    child.foreground = null
                }
            }
        }
    }

}
