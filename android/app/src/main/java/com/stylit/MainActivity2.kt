package com.stylit

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.stylit.databinding.ActivityMainBinding

class MainActivity2 : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Config action bar
        setSupportActionBar(activityMainBinding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as
                    NavHostFragment
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.permissions_fragment, R.id.camera_fragment
        ).build()
        NavigationUI.setupActionBarWithNavController(
            this, navHostFragment.navController, appBarConfiguration
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.fragment_container)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}