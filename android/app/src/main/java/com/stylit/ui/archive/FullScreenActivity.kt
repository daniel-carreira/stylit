package com.stylit.ui.archive

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stylit.R

class FullScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)

        val imageUri = intent.getStringExtra("uri") // Retrieve the passed variables

        if (savedInstanceState == null) {
            val fragment = TransformationFragment()
            val bundle = Bundle()
            bundle.putString("uri", imageUri) // Pass the variables to the fragment
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}
