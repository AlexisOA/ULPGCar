package com.alexisoa.ulpgcar.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var floatinButton : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        bottomAppBar = findViewById(R.id.bottomAppBar)
        floatinButton = findViewById(R.id.fab)
        navController = findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.menu.getItem(2).isEnabled = false

        floatinButton.setOnClickListener {
            navController.navigate(R.id.navigation_publisher)
        }
        AppBarConfiguration(setOf(R.id.navigation_search, R.id.navigation_travels, R.id.navigation_chat, R.id.navigation_profile))

        navView.setupWithNavController(navController)
        setBottomNavVisibility()

    }

    private fun checkNotification() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.nav_view)
        if (prefs.getNotiChat().equals("yes")){
            var badge = bottomNavigation.getOrCreateBadge(R.id.navigation_chat)
            badge.isVisible = true
        }

        if (prefs.getNotiReserve().equals("yes")){
            var badge = bottomNavigation.getOrCreateBadge(R.id.navigation_travels)
            badge.isVisible = true
        }
        if (prefs.getNotiRequest().equals("yes")){
            var badge = bottomNavigation.getOrCreateBadge(R.id.navigation_travels)
            badge.isVisible = true
        }
        if (prefs.getNotiReview().equals("yes")){
            var badge = bottomNavigation.getOrCreateBadge(R.id.navigation_profile)
            badge.isVisible = true
        }

    }


    private fun setBottomNavVisibility(){
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.initFragment -> hideBottomNav()
                R.id.loginFragment -> hideBottomNav()
                R.id.registerFormFragment -> hideBottomNav()
                R.id.resetPasswordFragment -> hideBottomNav()
                R.id.navigation_places -> hideBottomNav()
                R.id.navigation_modelCar -> hideBottomNav()
                R.id.navigation_edit_travels->hideBottomNav()
                R.id.travelsDetailsFragment -> hideBottomNav()
                R.id.navigation_mypublishdetails -> hideBottomNav()
                R.id.navigation_searchList->hideBottomNav()
                R.id.navigation_message->hideBottomNav()
                R.id.navigation_publish_date->hideBottomNav()
                R.id.navigation_hour_fragment->hideBottomNav()
                R.id.navigation_passenge->hideBottomNav()
                R.id.navigation_preferences->hideBottomNav()
                R.id.navigation_features_vehicle->hideBottomNav()
                R.id.navigation_price_fragment->hideBottomNav()
                R.id.navigation_reserve_details ->hideBottomNav()
                R.id.navigation_request_fragment->hideBottomNav()
                R.id.navigate_image_edit -> hideBottomNav()
                R.id.navigations_maps -> hideBottomNav()
                R.id.navigations_maps->hideBottomNav()
                R.id.navigation_hour_arrived->hideBottomNav()
                R.id.navigation_reviewFragment -> hideBottomNav()
                R.id.navigation_profileDetailsFragment->hideBottomNav()
                R.id.navigation_descriptionFormFragment->hideBottomNav()
                R.id.navigation_passwordFormFragment->hideBottomNav()
                R.id.navigation_reviewsReceivedFragment->hideBottomNav()
                else -> showBottomNav()
            }
        }
    }

    private fun hideBottomNav(){
        nav_view.visibility = View.GONE
        floatinButton.visibility = View.GONE
        bottomAppBar.visibility = View.GONE
        //bottomAppBar.performHide()
    }

    private fun showBottomNav(){
        nav_view.visibility = View.VISIBLE
        floatinButton.visibility = View.VISIBLE
        bottomAppBar.visibility = View.VISIBLE
        //bottomAppBar.performShow()
        checkNotification()
    }


}