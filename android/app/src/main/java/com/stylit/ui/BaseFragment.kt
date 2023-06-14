package com.stylit.ui

import ArchiveFragment
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.stylit.MainActivity2
import com.stylit.R
import com.stylit.databinding.FragmentBaseBinding
import com.stylit.ui.archive.TakePhoto
import com.stylit.ui.archive.TransformationFragment
import com.stylit.ui.home.HomeFragment
import com.stylit.ui.profile.ProfileFragment

class BaseFragment : Fragment() {
    private lateinit var binding: FragmentBaseBinding

    private val homeFragment = HomeFragment()
    private val takephotoFragment = TakePhoto()
    private val archiveFragment = ArchiveFragment()
    private val profileFragment = ProfileFragment()
    private val transformFragment = TransformationFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseBinding.inflate(layoutInflater, container, false)
        navigateTo(homeFragment)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    navigateTo(homeFragment)
                    true
                }
                R.id.takephoto -> {
                    navigateTo(transformFragment)
                    true
                }
                R.id.archive -> {
                    navigateTo(archiveFragment)
                    true
                }
                R.id.profile -> {
                    navigateTo(profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    private fun navigateTo(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .disallowAddToBackStack()
            .commit()
    }
}