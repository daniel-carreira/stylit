package com.stylit.ui.archive

import ArchiveFragment
import android.content.ContentValues
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.stylit.R
import com.stylit.adapter.StyleAdapter
import com.stylit.databinding.FragmentTransformationBinding
import com.stylit.helper.StyleTransferHelper
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class TransformationFragment : Fragment(), StyleTransferHelper.StyleTransferListener {

    private var _fragmentTransformationBinding: FragmentTransformationBinding? = null
    private val fragmentTransformationBinding get() = _fragmentTransformationBinding!!
    private lateinit var styleTransferHelper: StyleTransferHelper
    private lateinit var originalBitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _fragmentTransformationBinding =
            FragmentTransformationBinding.inflate(inflater, container, false)

        // Load Content Image to Bitmap
        val imagePath = arguments?.getString("uri")!!
        val bitmap = BitmapFactory.decodeFile(imagePath)

        originalBitmap = bitmap

        return fragmentTransformationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        styleTransferHelper = StyleTransferHelper(
            context = requireContext(),
            styleTransferListener = this
        )

        Glide.with(requireActivity())
            .load(originalBitmap)
            .centerCrop()
            .into(fragmentTransformationBinding.imgStyled)

        // Setup list style image
        getListStyle().let { styles ->
            with(fragmentTransformationBinding.recyclerViewStyle) {
                val linearLayoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL, false
                )
                layoutManager = linearLayoutManager

                val dividerItemDecoration = DividerItemDecoration(
                    context,
                    linearLayoutManager.orientation
                )
                dividerItemDecoration.setDrawable(
                    ContextCompat.getDrawable
                        (context, R.drawable.decoration_divider)!!
                )
                addItemDecoration(dividerItemDecoration)
                adapter = StyleAdapter(styles) { pos ->
                    getBitmapFromAssets(
                        "thumbnails/${styles[pos].imagePath}"
                    )?.let {
                        styleTransferHelper.setStyleImage(it)
                        styleTransferHelper.transfer(originalBitmap)
                    }
                }.apply {
                    // Set default style image
                    setSelected(0, true)
                    getBitmapFromAssets("thumbnails/${styles[0].imagePath}")?.let {
                        styleTransferHelper.setStyleImage(it)
                        styleTransferHelper.transfer(originalBitmap)
                    }
                }
            }
        }



        fragmentTransformationBinding.btnTransfer.setOnClickListener {
            val imageViewFullScreen = fragmentTransformationBinding.imgStyled
            saveImageToGallery(imageViewFullScreen.drawable.toBitmap())

            activity?.finish()
        }
    }

    override fun onError(error: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResult(bitmap: Bitmap, inferenceTime: Long) {
        activity?.runOnUiThread {
            Glide.with(requireContext()).load(bitmap).centerCrop()
                .into(fragmentTransformationBinding.imgStyled)
            fragmentTransformationBinding.inferenceTimeVal.text =
                String.format("%d ms", inferenceTime)
        }
    }

    private fun getListStyle(): MutableList<StyleAdapter.Style> {
        val styles = mutableListOf<StyleAdapter.Style>()
        //styles.add(StyleAdapter.Style("custom_image", isSelected = true))
        requireActivity().assets.list("thumbnails")?.forEach {
            styles.add(StyleAdapter.Style(it))
        }
        return styles
    }

    private fun getBitmapFromAssets(fileName: String): Bitmap? {
        val assetManager: AssetManager = requireActivity().assets
        return try {
            val istr: InputStream = assetManager.open(fileName)
            val bitmap = BitmapFactory.decodeStream(istr)
            istr.close()
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap?) {
        val resolver = context?.contentResolver
        val imageCollection =
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${UUID.randomUUID()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        val uri = resolver?.insert(imageCollection, contentValues)
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
}