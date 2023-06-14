package com.stylit.ui.archive

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.stylit.R
import com.stylit.adapter.StyleAdapter
import com.stylit.databinding.FragmentTransformationBinding
import com.stylit.helper.StyleTransferHelper
import java.io.InputStream

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
                    }
                }.apply {
                    // Set default style image
                    setSelected(0, true)
                    getBitmapFromAssets("thumbnails/${styles[0].imagePath}")?.let {
                        styleTransferHelper.setStyleImage(it)
                    }
                }
            }
        }

        Glide.with(requireActivity())
            .load(originalBitmap)
            .centerCrop()
            .into(fragmentTransformationBinding.imgStyled)

        fragmentTransformationBinding.btnTransfer.setOnClickListener {
            styleTransferHelper.transfer(originalBitmap)
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
}