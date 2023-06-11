package com.stylit

import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.stylit.ui.BaseFragment
import com.stylit.ui.signin.SignInFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isAuthenticated = FirebaseAuth.getInstance().currentUser != null

        if (isAuthenticated) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, BaseFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, SignInFragment())
                .commit()
        }
    }
}