package com.stylit.ui.archive

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import com.stylit.R

class ArchiveAdapter(private val myContext: Context, public val imageUris: List<String>) : BaseAdapter() {

    public val imageArray = intArrayOf(
        R.drawable.image1, R.drawable.image2, R.drawable.image1,
        R.drawable.image2, R.drawable.image1, R.drawable.image1,
        R.drawable.image2, R.drawable.image1, R.drawable.image1,
        R.drawable.image1, R.drawable.image1, R.drawable.image1,
        R.drawable.image1, R.drawable.image1, R.drawable.image1,
        R.drawable.image1, R.drawable.image1, R.drawable.image1,
        R.drawable.image1, R.drawable.image1, R.drawable.image1,
        R.drawable.image1, R.drawable.image1, R.drawable.image1,
        R.drawable.image1, R.drawable.image1, R.drawable.image1,
        R.drawable.image1, R.drawable.image1, R.drawable.image1
    )

    override fun getCount(): Int {
        return imageUris.size
    }

    override fun getItem(position: Int): Any {
        return imageUris[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView = ImageView(myContext)
        imageView.setImageURI(Uri.parse(imageUris[position]))
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.layoutParams = ViewGroup.LayoutParams(340, 350)

        return imageView
    }
}