package com.stylit.ui.archive

import ArchiveFragment
import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.stylit.R
import com.stylit.databinding.FragmentTakePhotoBinding
import java.io.IOException
import java.io.OutputStream
import java.util.*


class TakePhoto : Fragment() {
    private lateinit var binding: FragmentTakePhotoBinding
    private lateinit var mPermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mGetImage: androidx.activity.result.ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null
    private val CAMERA = 100


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTakePhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPermissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {


        }


        mGetImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == androidx.appcompat.app.AppCompatActivity.RESULT_OK && result.data != null) {
                    val bundle: Bundle? = result.data?.extras
                    val bitmap = bundle?.get("data") as Bitmap?
                    saveImageToGallery(bitmap)
                }
            }

        requestCameraPermission()

        binding.saveButton.setOnClickListener {
            openCamera()
        }

        binding.galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 3)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            Log.d("MyLogs", "WGWEGEWG clicked")
            val bitmap = data.data?.let { uri ->
                try {
                    val inputStream = context?.contentResolver?.openInputStream(uri)
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }
            bitmap?.let { saveImageToGallery(it) }
        }
    }



    private fun requestCameraPermission() {
        val isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!isCameraPermissionGranted) {
            mPermissionResultLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        mGetImage.launch(intent)
    }

    private fun saveImageToGallery(bitmap: Bitmap?) {
        val resolver = requireContext().contentResolver
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
                requireContext(),
                "Image saved successfully",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Failed to save image: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
        val takePhotoFragment = ArchiveFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.container, takePhotoFragment)
            .commit()
    }
}