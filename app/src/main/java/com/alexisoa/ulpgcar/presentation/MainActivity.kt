package com.alexisoa.ulpgcar.presentation

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.databinding.ActivityMainBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication
import com.example.ulpgcarprototype.data.model.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile))
        //setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
        setBottomNavVisibility()

    }

    private fun setBottomNavVisibility(){
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.initFragment -> hideBottomNav()
                R.id.loginFragment -> hideBottomNav()
                R.id.registerFragment -> hideBottomNav()
                else -> {
                    val user = User(UserSharedApplication.prefs.getEmail(), UserSharedApplication.prefs.getProvider())
                    val argument = NavArgument.Builder().setDefaultValue(user).build()
                    destination.addArgument("user", argument)
                    showBottomNav()
                }
            }
        }
    }

    private fun hideBottomNav(){
        nav_view.visibility = View.GONE
    }

    private fun showBottomNav(){
        nav_view.visibility = View.VISIBLE
    }
}