package com.udacity.project4.locationreminders

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.authentication.AuthenticationState
import com.udacity.project4.authentication.AuthenticationViewModel
import kotlinx.android.synthetic.main.activity_reminders.*

/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {

    private val TAG = RemindersActivity::class.java.simpleName

    private lateinit var navController: NavController

    private val viewModel by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeAuthentication()

        setContentView(R.layout.activity_reminders)
        navController = (nav_host_fragment as NavHostFragment).navController
    }

    private fun observeAuthentication() {
        viewModel.authenticationState.observe(this, Observer{ authenticateState ->
            when (authenticateState) {
                AuthenticationState.AUTHENTICATED -> {
                    // Go to Reminder Activity
                    Log.d(TAG, "onCreate: Authenticated")
                }
                else -> {
                    // Do nothing
                    Log.d(TAG, "onCreate: Unauthenticated")
                    startActivity(Intent(
                        this@RemindersActivity,
                        AuthenticationActivity::class.java)
                    )
                }
            }
        })

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navController.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
