package com.stylit.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.stylit.R
import com.stylit.databinding.FragmentProfileBinding
import com.stylit.ui.signin.SignInFragment

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        // Photo
        Picasso.get().load(auth.currentUser?.photoUrl).resize(0, 500).into(binding.avatar)
        // Name
        binding.txtProfileName.text = auth.currentUser?.displayName
        // Email
        binding.txtProfileEmail.text = auth.currentUser?.email

        // Sign Out button action
        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            parentFragmentManager.beginTransaction()
                .replace(android.R.id.content, SignInFragment())
                .commit()
        }
    }
}