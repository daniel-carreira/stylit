package com.stylit.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.lifecycle.ViewModel
import com.stylit.utils.SingleLiveEvent
import java.nio.ByteBuffer

class ArchiveViewModel : ViewModel() {
    private val _inputBitmap = SingleLiveEvent<Bitmap>()
    val inputBitmap get() = _inputBitmap

    // Store helper setting
    var defaultModelNumThreads: Int = 2
    var defaultModel: Int = 0

    // Convert bytebuffer to Bitmap and rotate for ready to show on Ui and
    // transfer
    fun setInputImage(buffer: Bitmap) {
        _inputBitmap.postValue(buffer)
    }

    fun getInputBitmap() = _inputBitmap.value
}