package com.stylit

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.stylit.ui.archive.TransformationFragment
import com.stylit.viewmodel.ArchiveViewModel

class FullScreenActivity : AppCompatActivity() {
    private val viewModel: ArchiveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)

        val imageId = intent.getStringExtra("uri") // Retrieve the passed variables

        if (savedInstanceState == null) {
            val fragment = TransformationFragment()
            val bundle = Bundle()
            bundle.putString("uri", imageId) // Pass the variables to the fragment
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}
